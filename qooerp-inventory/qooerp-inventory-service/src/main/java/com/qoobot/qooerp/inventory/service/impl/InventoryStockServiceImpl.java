package com.qoobot.qooerp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.*;
import com.qoobot.qooerp.inventory.entity.InventoryMaterial;
import com.qoobot.qooerp.inventory.entity.InventoryStock;
import com.qoobot.qooerp.inventory.entity.InventoryStockRecord;
import com.qoobot.qooerp.inventory.mapper.*;
import com.qoobot.qooerp.inventory.service.InventoryStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryStockServiceImpl implements InventoryStockService {

    private final InventoryStockMapper stockMapper;
    private final InventoryMaterialMapper materialMapper;
    private final InventoryStockRecordMapper recordMapper;
    private final InventoryWarehouseMapper warehouseMapper;

    @Override
    public StockDTO getStockById(Long id) {
        InventoryStock stock = stockMapper.selectById(id);
        if (stock == null) {
            throw new RuntimeException("库存不存在");
        }
        return convertToDTO(stock);
    }

    @Override
    public Page<StockDTO> queryStocks(StockQueryDTO dto) {
        IPage<InventoryStock> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<InventoryStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryStock::getTenantId, getCurrentTenantId())
                .eq(dto.getWarehouseId() != null, InventoryStock::getWarehouseId, dto.getWarehouseId())
                .eq(dto.getMaterialId() != null, InventoryStock::getMaterialId, dto.getMaterialId())
                .eq(dto.getStockStatus() != null, InventoryStock::getStockStatus, dto.getStockStatus());
        IPage<InventoryStock> result = stockMapper.selectPage(page, wrapper);

        Page<StockDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustStock(StockAdjustDTO dto) {
        InventoryStock stock = stockMapper.selectById(dto.getStockId());
        if (stock == null) {
            throw new RuntimeException("库存不存在");
        }

        BigDecimal beforeQuantity = stock.getQuantity();
        BigDecimal changeQuantity = dto.getAdjustQuantity();
        BigDecimal afterQuantity = stock.getQuantity().add(changeQuantity);

        if (afterQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("库存不能为负数");
        }

        stock.setQuantity(afterQuantity);
        stock.setAvailableQuantity(afterQuantity.subtract(stock.getLockedQuantity()));
        stock.setUpdateTime(LocalDateTime.now());
        stockMapper.updateById(stock);

        recordMapper.insert(createRecord("ADJUST", "STOCK_ADJUST", null,
                stock.getWarehouseId(), stock.getMaterialId(), dto.getBatchNo(),
                beforeQuantity, changeQuantity, afterQuantity,
                getCurrentUserId(), "System"));
    }

    @Override
    public List<StockAlertDTO> getStockAlerts() {
        List<StockAlertDTO> alerts = new ArrayList<>();

        LambdaQueryWrapper<InventoryStock> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(InventoryStock::getTenantId, getCurrentTenantId());
        List<InventoryStock> stocks = stockMapper.selectList(stockWrapper);

        for (InventoryStock stock : stocks) {
            InventoryMaterial material = materialMapper.selectById(stock.getMaterialId());
            if (material != null && material.getSafetyStock() != null) {
                StockAlertDTO alert = new StockAlertDTO();
                alert.setMaterialId(material.getId());
                alert.setMaterialCode(material.getMaterialCode());
                alert.setMaterialName(material.getMaterialName());
                alert.setCurrentStock(stock.getQuantity());
                alert.setSafetyStock(material.getSafetyStock());
                alert.setMaxStock(material.getMaxStock());

                if (stock.getQuantity().compareTo(material.getSafetyStock()) < 0) {
                    alert.setAlertType("LOW_STOCK");
                    alert.setAlertMessage("库存低于安全库存");
                    alerts.add(alert);
                } else if (material.getMaxStock() != null &&
                           stock.getQuantity().compareTo(material.getMaxStock()) > 0) {
                    alert.setAlertType("HIGH_STOCK");
                    alert.setAlertMessage("库存超过最大库存");
                    alerts.add(alert);
                }
            }
        }

        return alerts;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(Long stockId, Long quantity) {
        InventoryStock stock = stockMapper.selectById(stockId);
        if (stock == null) {
            throw new RuntimeException("库存不存在");
        }

        BigDecimal lockQty = BigDecimal.valueOf(quantity);
        if (stock.getAvailableQuantity().compareTo(lockQty) < 0) {
            throw new RuntimeException("可用库存不足");
        }

        stock.setLockedQuantity(stock.getLockedQuantity().add(lockQty));
        stock.setAvailableQuantity(stock.getAvailableQuantity().subtract(lockQty));
        stock.setUpdateTime(LocalDateTime.now());
        stockMapper.updateById(stock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockStock(Long stockId, Long quantity) {
        InventoryStock stock = stockMapper.selectById(stockId);
        if (stock == null) {
            throw new RuntimeException("库存不存在");
        }

        BigDecimal unlockQty = BigDecimal.valueOf(quantity);
        BigDecimal newLocked = stock.getLockedQuantity().subtract(unlockQty);
        if (newLocked.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("解锁数量超过锁定数量");
        }

        stock.setLockedQuantity(newLocked);
        stock.setAvailableQuantity(stock.getAvailableQuantity().add(unlockQty));
        stock.setUpdateTime(LocalDateTime.now());
        stockMapper.updateById(stock);
    }

    private StockDTO convertToDTO(InventoryStock entity) {
        StockDTO dto = new StockDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private InventoryStockRecord createRecord(String recordType, String businessType, String businessNo,
                                               Long warehouseId, Long materialId, String batchNo,
                                               BigDecimal beforeQty, BigDecimal changeQty, BigDecimal afterQty,
                                               Long operatorId, String operatorName) {
        InventoryStockRecord record = new InventoryStockRecord();
        record.setTenantId(getCurrentTenantId());
        record.setRecordType(recordType);
        record.setBusinessType(businessType);
        record.setBusinessNo(businessNo);
        record.setWarehouseId(warehouseId);
        record.setMaterialId(materialId);
        record.setBatchNo(batchNo);
        record.setBeforeQuantity(beforeQty);
        record.setChangeQuantity(changeQty);
        record.setAfterQuantity(afterQty);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setRecordTime(LocalDateTime.now());
        return record;
    }

    private Long getCurrentTenantId() {
        return 1L;
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
