package com.qoobot.qooerp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.*;
import com.qoobot.qooerp.inventory.entity.*;
import com.qoobot.qooerp.inventory.mapper.*;
import com.qoobot.qooerp.inventory.service.InventoryStockOutboundService;
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
public class InventoryStockOutboundServiceImpl implements InventoryStockOutboundService {

    private final InventoryStockOutboundMapper outboundMapper;
    private final InventoryStockOutboundDetailMapper outboundDetailMapper;
    private final InventoryStockMapper stockMapper;
    private final InventoryStockRecordMapper recordMapper;
    private final InventoryMaterialMapper materialMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOutbound(OutboundCreateDTO dto) {
        String outboundNo = generateOutboundNo();

        InventoryStockOutbound outbound = new InventoryStockOutbound();
        outbound.setTenantId(getCurrentTenantId());
        outbound.setOutboundNo(outboundNo);
        outbound.setOutboundType(dto.getOutboundType());
        outbound.setWarehouseId(dto.getWarehouseId());
        outbound.setCustomerId(dto.getCustomerId());
        outbound.setOutboundStatus("DRAFT");
        outbound.setTotalQuantity(BigDecimal.ZERO);
        outbound.setTotalAmount(BigDecimal.ZERO);
        outbound.setCreateBy(getCurrentUserId());
        outbound.setCreateTime(LocalDateTime.now());
        outboundMapper.insert(outbound);

        BigDecimal totalQuantity = BigDecimal.ZERO;

        for (OutboundDetailCreateDTO detailDTO : dto.getDetails()) {
            InventoryMaterial material = materialMapper.selectById(detailDTO.getMaterialId());
            if (material == null) {
                throw new RuntimeException("物料不存在");
            }

            InventoryStockOutboundDetail detail = new InventoryStockOutboundDetail();
            detail.setTenantId(getCurrentTenantId());
            detail.setOutboundId(outbound.getId());
            detail.setOutboundNo(outboundNo);
            detail.setMaterialId(detailDTO.getMaterialId());
            detail.setMaterialCode(material.getMaterialCode());
            detail.setMaterialName(material.getMaterialName());
            detail.setMaterialSpec(material.getMaterialSpec());
            detail.setMaterialUnit(material.getMaterialUnit());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setBatchNo(detailDTO.getBatchNo());
            detail.setActualQuantity(detailDTO.getQuantity());
            detail.setCreateTime(LocalDateTime.now());
            outboundDetailMapper.insert(detail);

            totalQuantity = totalQuantity.add(detail.getQuantity());
        }

        outbound.setTotalQuantity(totalQuantity);
        outboundMapper.updateById(outbound);

        return outbound.getId();
    }

    @Override
    public void submitOutbound(Long id) {
        InventoryStockOutbound outbound = outboundMapper.selectById(id);
        if (outbound == null) {
            throw new RuntimeException("出库单不存在");
        }
        outbound.setOutboundStatus("PENDING");
        outbound.setUpdateBy(getCurrentUserId());
        outbound.setUpdateTime(LocalDateTime.now());
        outboundMapper.updateById(outbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOutbound(Long id, Boolean approved) {
        InventoryStockOutbound outbound = outboundMapper.selectById(id);
        if (outbound == null) {
            throw new RuntimeException("出库单不存在");
        }

        if (approved) {
            outbound.setOutboundStatus("APPROVED");
            outbound.setAuditorId(getCurrentUserId());
            outbound.setAuditorName("System");
            outbound.setAuditTime(LocalDateTime.now());
            outbound.setUpdateTime(LocalDateTime.now());
            outboundMapper.updateById(outbound);
        } else {
            outbound.setOutboundStatus("REJECTED");
            outbound.setAuditorId(getCurrentUserId());
            outbound.setAuditTime(LocalDateTime.now());
            outbound.setUpdateTime(LocalDateTime.now());
            outboundMapper.updateById(outbound);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeOutbound(Long id) {
        InventoryStockOutbound outbound = outboundMapper.selectById(id);
        if (outbound == null) {
            throw new RuntimeException("出库单不存在");
        }

        if (!"APPROVED".equals(outbound.getOutboundStatus())) {
            throw new RuntimeException("出库单未审核通过");
        }

        List<InventoryStockOutboundDetail> details = outboundDetailMapper.selectList(
                new LambdaQueryWrapper<InventoryStockOutboundDetail>()
                        .eq(InventoryStockOutboundDetail::getOutboundId, id)
        );

        for (InventoryStockOutboundDetail detail : details) {
            LambdaQueryWrapper<InventoryStock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(InventoryStock::getWarehouseId, outbound.getWarehouseId())
                    .eq(InventoryStock::getMaterialId, detail.getMaterialId())
                    .eq(InventoryStock::getBatchNo, detail.getBatchNo());
            InventoryStock stock = stockMapper.selectOne(stockWrapper);

            if (stock == null || stock.getAvailableQuantity().compareTo(detail.getQuantity()) < 0) {
                throw new RuntimeException("库存不足: " + detail.getMaterialCode());
            }

            BigDecimal beforeQuantity = stock.getQuantity();
            BigDecimal afterQuantity = beforeQuantity.subtract(detail.getActualQuantity());

            stock.setQuantity(afterQuantity);
            stock.setAvailableQuantity(stock.getAvailableQuantity().subtract(detail.getActualQuantity()));
            stock.setUpdateTime(LocalDateTime.now());
            stockMapper.updateById(stock);

            recordMapper.insert(createRecord("OUTBOUND", "STOCK_OUTBOUND", outbound.getOutboundNo(),
                    outbound.getWarehouseId(), detail.getMaterialId(), detail.getBatchNo(),
                    beforeQuantity, detail.getActualQuantity().negate(), afterQuantity,
                    getCurrentUserId(), "System"));
        }

        outbound.setOutboundStatus("COMPLETED");
        outbound.setOutboundDate(LocalDateTime.now());
        outbound.setUpdateTime(LocalDateTime.now());
        outboundMapper.updateById(outbound);
    }

    @Override
    public OutboundDTO getOutboundById(Long id) {
        InventoryStockOutbound outbound = outboundMapper.selectById(id);
        if (outbound == null) {
            throw new RuntimeException("出库单不存在");
        }

        OutboundDTO dto = convertToDTO(outbound);

        List<InventoryStockOutboundDetail> details = outboundDetailMapper.selectList(
                new LambdaQueryWrapper<InventoryStockOutboundDetail>()
                        .eq(InventoryStockOutboundDetail::getOutboundId, id)
        );
        dto.setDetails(details.stream().map(this::convertDetailToDTO).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public Page<OutboundDTO> queryOutbounds(OutboundQueryDTO dto) {
        IPage<InventoryStockOutbound> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<InventoryStockOutbound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryStockOutbound::getTenantId, getCurrentTenantId())
                .like(dto.getOutboundNo() != null, InventoryStockOutbound::getOutboundNo, dto.getOutboundNo())
                .eq(dto.getOutboundType() != null, InventoryStockOutbound::getOutboundType, dto.getOutboundType())
                .eq(dto.getWarehouseId() != null, InventoryStockOutbound::getWarehouseId, dto.getWarehouseId())
                .eq(dto.getCustomerId() != null, InventoryStockOutbound::getCustomerId, dto.getCustomerId())
                .eq(dto.getOutboundStatus() != null, InventoryStockOutbound::getOutboundStatus, dto.getOutboundStatus())
                .ge(dto.getStartDate() != null, InventoryStockOutbound::getCreateTime, dto.getStartDate())
                .le(dto.getEndDate() != null, InventoryStockOutbound::getCreateTime, dto.getEndDate());
        IPage<InventoryStockOutbound> result = outboundMapper.selectPage(page, wrapper);

        Page<OutboundDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    private OutboundDTO convertToDTO(InventoryStockOutbound entity) {
        OutboundDTO dto = new OutboundDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private OutboundDetailDTO convertDetailToDTO(InventoryStockOutboundDetail entity) {
        OutboundDetailDTO dto = new OutboundDetailDTO();
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

    private String generateOutboundNo() {
        return "OUT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private Long getCurrentTenantId() {
        return 1L;
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
