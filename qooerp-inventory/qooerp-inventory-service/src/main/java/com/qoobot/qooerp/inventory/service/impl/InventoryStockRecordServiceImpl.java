package com.qoobot.qooerp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.StockRecordDTO;
import com.qoobot.qooerp.inventory.dto.StockRecordQueryDTO;
import com.qoobot.qooerp.inventory.entity.InventoryStockRecord;
import com.qoobot.qooerp.inventory.mapper.InventoryStockRecordMapper;
import com.qoobot.qooerp.inventory.service.InventoryStockRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryStockRecordServiceImpl implements InventoryStockRecordService {

    private final InventoryStockRecordMapper recordMapper;

    @Override
    public Page<StockRecordDTO> queryStockRecords(StockRecordQueryDTO dto) {
        IPage<InventoryStockRecord> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<InventoryStockRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryStockRecord::getTenantId, getCurrentTenantId())
                .eq(dto.getRecordType() != null, InventoryStockRecord::getRecordType, dto.getRecordType())
                .eq(dto.getBusinessType() != null, InventoryStockRecord::getBusinessType, dto.getBusinessType())
                .like(dto.getBusinessNo() != null, InventoryStockRecord::getBusinessNo, dto.getBusinessNo())
                .eq(dto.getWarehouseId() != null, InventoryStockRecord::getWarehouseId, dto.getWarehouseId())
                .eq(dto.getMaterialId() != null, InventoryStockRecord::getMaterialId, dto.getMaterialId())
                .like(dto.getMaterialCode() != null, InventoryStockRecord::getMaterialCode, dto.getMaterialCode())
                .ge(dto.getStartDate() != null, InventoryStockRecord::getRecordTime, dto.getStartDate())
                .le(dto.getEndDate() != null, InventoryStockRecord::getRecordTime, dto.getEndDate())
                .orderByDesc(InventoryStockRecord::getRecordTime);
        IPage<InventoryStockRecord> result = recordMapper.selectPage(page, wrapper);

        Page<StockRecordDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public void addStockRecord(String recordType, String businessType, String businessNo,
                              Long warehouseId, Long materialId, String batchNo,
                              Long beforeQuantity, Long changeQuantity, Long afterQuantity,
                              Long operatorId, String operatorName) {
        InventoryStockRecord record = new InventoryStockRecord();
        record.setTenantId(getCurrentTenantId());
        record.setRecordType(recordType);
        record.setBusinessType(businessType);
        record.setBusinessNo(businessNo);
        record.setWarehouseId(warehouseId);
        record.setMaterialId(materialId);
        record.setBatchNo(batchNo);
        record.setBeforeQuantity(java.math.BigDecimal.valueOf(beforeQuantity));
        record.setChangeQuantity(java.math.BigDecimal.valueOf(changeQuantity));
        record.setAfterQuantity(java.math.BigDecimal.valueOf(afterQuantity));
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setRecordTime(java.time.LocalDateTime.now());
        recordMapper.insert(record);
    }

    private StockRecordDTO convertToDTO(InventoryStockRecord entity) {
        StockRecordDTO dto = new StockRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private Long getCurrentTenantId() {
        return 1L;
    }
}
