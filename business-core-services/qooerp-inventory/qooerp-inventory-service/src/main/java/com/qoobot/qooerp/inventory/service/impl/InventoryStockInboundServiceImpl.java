package com.qoobot.qooerp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.*;
import com.qoobot.qooerp.inventory.entity.*;
import com.qoobot.qooerp.inventory.mapper.*;
import com.qoobot.qooerp.inventory.service.InventoryStockInboundService;
import com.qoobot.qooerp.inventory.service.InventoryStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryStockInboundServiceImpl implements InventoryStockInboundService {

    private final InventoryStockInboundMapper inboundMapper;
    private final InventoryStockInboundDetailMapper inboundDetailMapper;
    private final InventoryStockMapper stockMapper;
    private final InventoryStockRecordMapper recordMapper;
    private final InventoryMaterialMapper materialMapper;
    private final InventoryWarehouseMapper warehouseMapper;
    private final InventoryStockService stockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInbound(InboundCreateDTO dto) {
        String inboundNo = generateInboundNo();

        InventoryStockInbound inbound = new InventoryStockInbound();
        inbound.setTenantId(getCurrentTenantId());
        inbound.setInboundNo(inboundNo);
        inbound.setInboundType(dto.getInboundType());
        inbound.setWarehouseId(dto.getWarehouseId());
        inbound.setSupplierId(dto.getSupplierId());
        inbound.setInboundStatus("DRAFT");
        inbound.setTotalQuantity(BigDecimal.ZERO);
        inbound.setTotalAmount(BigDecimal.ZERO);
        inbound.setCreateBy(getCurrentUserId());
        inbound.setCreateTime(LocalDateTime.now());
        inboundMapper.insert(inbound);

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (InboundDetailCreateDTO detailDTO : dto.getDetails()) {
            InventoryMaterial material = materialMapper.selectById(detailDTO.getMaterialId());
            if (material == null) {
                throw new RuntimeException("物料不存在");
            }

            InventoryStockInboundDetail detail = new InventoryStockInboundDetail();
            detail.setTenantId(getCurrentTenantId());
            detail.setInboundId(inbound.getId());
            detail.setInboundNo(inboundNo);
            detail.setMaterialId(detailDTO.getMaterialId());
            detail.setMaterialCode(material.getMaterialCode());
            detail.setMaterialName(material.getMaterialName());
            detail.setMaterialSpec(material.getMaterialSpec());
            detail.setMaterialUnit(material.getMaterialUnit());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(detailDTO.getUnitPrice() != null ? detailDTO.getUnitPrice() : BigDecimal.ZERO);
            detail.setTotalPrice(detail.getQuantity().multiply(detail.getUnitPrice()));
            detail.setBatchNo(detailDTO.getBatchNo());
            detail.setProductionDate(detailDTO.getProductionDate());
            detail.setExpiryDate(detailDTO.getExpiryDate());
            detail.setActualQuantity(detailDTO.getQuantity());
            detail.setCreateTime(LocalDateTime.now());
            inboundDetailMapper.insert(detail);

            totalQuantity = totalQuantity.add(detail.getQuantity());
            totalAmount = totalAmount.add(detail.getTotalPrice());
        }

        inbound.setTotalQuantity(totalQuantity);
        inbound.setTotalAmount(totalAmount);
        inboundMapper.updateById(inbound);

        return inbound.getId();
    }

    @Override
    public void submitInbound(Long id) {
        InventoryStockInbound inbound = inboundMapper.selectById(id);
        if (inbound == null) {
            throw new RuntimeException("入库单不存在");
        }
        inbound.setInboundStatus("PENDING");
        inbound.setUpdateBy(getCurrentUserId());
        inbound.setUpdateTime(LocalDateTime.now());
        inboundMapper.updateById(inbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditInbound(Long id, Boolean approved) {
        InventoryStockInbound inbound = inboundMapper.selectById(id);
        if (inbound == null) {
            throw new RuntimeException("入库单不存在");
        }

        if (approved) {
            inbound.setInboundStatus("APPROVED");
            inbound.setAuditorId(getCurrentUserId());
            inbound.setAuditorName("System");
            inbound.setAuditTime(LocalDateTime.now());
            inbound.setUpdateTime(LocalDateTime.now());
            inboundMapper.updateById(inbound);
        } else {
            inbound.setInboundStatus("REJECTED");
            inbound.setAuditorId(getCurrentUserId());
            inbound.setAuditTime(LocalDateTime.now());
            inbound.setUpdateTime(LocalDateTime.now());
            inboundMapper.updateById(inbound);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeInbound(Long id) {
        InventoryStockInbound inbound = inboundMapper.selectById(id);
        if (inbound == null) {
            throw new RuntimeException("入库单不存在");
        }

        if (!"APPROVED".equals(inbound.getInboundStatus())) {
            throw new RuntimeException("入库单未审核通过");
        }

        List<InventoryStockInboundDetail> details = inboundDetailMapper.selectList(
                new LambdaQueryWrapper<InventoryStockInboundDetail>()
                        .eq(InventoryStockInboundDetail::getInboundId, id)
        );

        for (InventoryStockInboundDetail detail : details) {
            LambdaQueryWrapper<InventoryStock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(InventoryStock::getWarehouseId, inbound.getWarehouseId())
                    .eq(InventoryStock::getMaterialId, detail.getMaterialId())
                    .eq(InventoryStock::getBatchNo, detail.getBatchNo());
            InventoryStock stock = stockMapper.selectOne(stockWrapper);

            BigDecimal beforeQuantity = stock != null ? stock.getQuantity() : BigDecimal.ZERO;
            BigDecimal afterQuantity = beforeQuantity.add(detail.getActualQuantity());

            if (stock == null) {
                stock = new InventoryStock();
                stock.setTenantId(getCurrentTenantId());
                stock.setWarehouseId(inbound.getWarehouseId());
                stock.setMaterialId(detail.getMaterialId());
                stock.setQuantity(afterQuantity);
                stock.setAvailableQuantity(afterQuantity);
                stock.setLockedQuantity(BigDecimal.ZERO);
                stock.setInTransitQuantity(BigDecimal.ZERO);
                stock.setStockStatus("NORMAL");
                stock.setBatchNo(detail.getBatchNo());
                stock.setProductionDate(detail.getProductionDate());
                stock.setExpiryDate(detail.getExpiryDate());
                stock.setCreateTime(LocalDateTime.now());
                stock.setUpdateTime(LocalDateTime.now());
                stockMapper.insert(stock);
            } else {
                stock.setQuantity(afterQuantity);
                stock.setAvailableQuantity(stock.getAvailableQuantity().add(detail.getActualQuantity()));
                stock.setUpdateTime(LocalDateTime.now());
                stockMapper.updateById(stock);
            }

            recordMapper.insert(createRecord("INBOUND", "STOCK_INBOUND", inbound.getInboundNo(),
                    inbound.getWarehouseId(), detail.getMaterialId(), detail.getBatchNo(),
                    beforeQuantity, detail.getActualQuantity(), afterQuantity,
                    getCurrentUserId(), "System"));
        }

        inbound.setInboundStatus("COMPLETED");
        inbound.setInboundDate(LocalDateTime.now());
        inbound.setUpdateTime(LocalDateTime.now());
        inboundMapper.updateById(inbound);
    }

    @Override
    public InboundDTO getInboundById(Long id) {
        InventoryStockInbound inbound = inboundMapper.selectById(id);
        if (inbound == null) {
            throw new RuntimeException("入库单不存在");
        }

        InboundDTO dto = convertToDTO(inbound);

        List<InventoryStockInboundDetail> details = inboundDetailMapper.selectList(
                new LambdaQueryWrapper<InventoryStockInboundDetail>()
                        .eq(InventoryStockInboundDetail::getInboundId, id)
        );
        dto.setDetails(details.stream().map(this::convertDetailToDTO).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public Page<InboundDTO> queryInbounds(InboundQueryDTO dto) {
        IPage<InventoryStockInbound> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<InventoryStockInbound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryStockInbound::getTenantId, getCurrentTenantId())
                .like(dto.getInboundNo() != null, InventoryStockInbound::getInboundNo, dto.getInboundNo())
                .eq(dto.getInboundType() != null, InventoryStockInbound::getInboundType, dto.getInboundType())
                .eq(dto.getWarehouseId() != null, InventoryStockInbound::getWarehouseId, dto.getWarehouseId())
                .eq(dto.getSupplierId() != null, InventoryStockInbound::getSupplierId, dto.getSupplierId())
                .eq(dto.getInboundStatus() != null, InventoryStockInbound::getInboundStatus, dto.getInboundStatus())
                .ge(dto.getStartDate() != null, InventoryStockInbound::getCreateTime, dto.getStartDate())
                .le(dto.getEndDate() != null, InventoryStockInbound::getCreateTime, dto.getEndDate());
        IPage<InventoryStockInbound> result = inboundMapper.selectPage(page, wrapper);

        Page<InboundDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    private InboundDTO convertToDTO(InventoryStockInbound entity) {
        InboundDTO dto = new InboundDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private InboundDetailDTO convertDetailToDTO(InventoryStockInboundDetail entity) {
        InboundDetailDTO dto = new InboundDetailDTO();
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

    private String generateInboundNo() {
        return "INB" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private Long getCurrentTenantId() {
        return 1L;
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
