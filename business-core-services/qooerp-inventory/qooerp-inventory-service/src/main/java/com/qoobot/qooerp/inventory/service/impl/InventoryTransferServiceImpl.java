package com.qoobot.qooerp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.*;
import com.qoobot.qooerp.inventory.entity.*;
import com.qoobot.qooerp.inventory.mapper.*;
import com.qoobot.qooerp.inventory.service.InventoryTransferService;
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
public class InventoryTransferServiceImpl implements InventoryTransferService {

    private final InventoryTransferMapper transferMapper;
    private final InventoryTransferDetailMapper transferDetailMapper;
    private final InventoryStockMapper stockMapper;
    private final InventoryStockRecordMapper recordMapper;
    private final InventoryMaterialMapper materialMapper;
    private final InventoryWarehouseMapper warehouseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTransfer(TransferCreateDTO dto) {
        if (dto.getSourceWarehouseId().equals(dto.getTargetWarehouseId())) {
            throw new RuntimeException("源仓库和目标仓库不能相同");
        }

        String transferNo = generateTransferNo();

        InventoryTransfer transfer = new InventoryTransfer();
        transfer.setTenantId(getCurrentTenantId());
        transfer.setTransferNo(transferNo);
        transfer.setTransferType(dto.getTransferType());
        transfer.setSourceWarehouseId(dto.getSourceWarehouseId());
        transfer.setTargetWarehouseId(dto.getTargetWarehouseId());
        transfer.setTransferStatus("DRAFT");
        transfer.setTotalQuantity(BigDecimal.ZERO);
        transfer.setCreateBy(getCurrentUserId());
        transfer.setCreateTime(LocalDateTime.now());
        transferMapper.insert(transfer);

        BigDecimal totalQuantity = BigDecimal.ZERO;

        for (TransferDetailCreateDTO detailDTO : dto.getDetails()) {
            InventoryMaterial material = materialMapper.selectById(detailDTO.getMaterialId());
            if (material == null) {
                throw new RuntimeException("物料不存在");
            }

            InventoryTransferDetail detail = new InventoryTransferDetail();
            detail.setTenantId(getCurrentTenantId());
            detail.setTransferId(transfer.getId());
            detail.setTransferNo(transferNo);
            detail.setMaterialId(detailDTO.getMaterialId());
            detail.setMaterialCode(material.getMaterialCode());
            detail.setMaterialName(material.getMaterialName());
            detail.setMaterialSpec(material.getMaterialSpec());
            detail.setMaterialUnit(material.getMaterialUnit());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setBatchNo(detailDTO.getBatchNo());
            detail.setActualQuantity(detailDTO.getQuantity());
            detail.setCreateTime(LocalDateTime.now());
            transferDetailMapper.insert(detail);

            totalQuantity = totalQuantity.add(detail.getQuantity());
        }

        transfer.setTotalQuantity(totalQuantity);
        transferMapper.updateById(transfer);

        return transfer.getId();
    }

    @Override
    public void submitTransfer(Long id) {
        InventoryTransfer transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw new RuntimeException("调拨单不存在");
        }
        transfer.setTransferStatus("PENDING");
        transfer.setUpdateBy(getCurrentUserId());
        transfer.setUpdateTime(LocalDateTime.now());
        transferMapper.updateById(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditTransfer(Long id, Boolean approved) {
        InventoryTransfer transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw new RuntimeException("调拨单不存在");
        }

        if (approved) {
            transfer.setTransferStatus("APPROVED");
            transfer.setAuditorId(getCurrentUserId());
            transfer.setAuditorName("System");
            transfer.setAuditTime(LocalDateTime.now());
            transfer.setUpdateTime(LocalDateTime.now());
            transferMapper.updateById(transfer);
        } else {
            transfer.setTransferStatus("REJECTED");
            transfer.setAuditorId(getCurrentUserId());
            transfer.setAuditTime(LocalDateTime.now());
            transfer.setUpdateTime(LocalDateTime.now());
            transferMapper.updateById(transfer);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeTransfer(Long id) {
        InventoryTransfer transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw new RuntimeException("调拨单不存在");
        }

        if (!"APPROVED".equals(transfer.getTransferStatus())) {
            throw new RuntimeException("调拨单未审核通过");
        }

        List<InventoryTransferDetail> details = transferDetailMapper.selectList(
                new LambdaQueryWrapper<InventoryTransferDetail>()
                        .eq(InventoryTransferDetail::getTransferId, id)
        );

        for (InventoryTransferDetail detail : details) {
            LambdaQueryWrapper<InventoryStock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(InventoryStock::getWarehouseId, transfer.getSourceWarehouseId())
                    .eq(InventoryStock::getMaterialId, detail.getMaterialId())
                    .eq(InventoryStock::getBatchNo, detail.getBatchNo());
            InventoryStock sourceStock = stockMapper.selectOne(stockWrapper);

            if (sourceStock == null || sourceStock.getAvailableQuantity().compareTo(detail.getQuantity()) < 0) {
                throw new RuntimeException("源仓库库存不足: " + detail.getMaterialCode());
            }

            BigDecimal beforeSourceQuantity = sourceStock.getQuantity();
            BigDecimal afterSourceQuantity = beforeSourceQuantity.subtract(detail.getActualQuantity());

            sourceStock.setQuantity(afterSourceQuantity);
            sourceStock.setAvailableQuantity(sourceStock.getAvailableQuantity().subtract(detail.getActualQuantity()));
            sourceStock.setUpdateTime(LocalDateTime.now());
            stockMapper.updateById(sourceStock);

            LambdaQueryWrapper<InventoryStock> targetStockWrapper = new LambdaQueryWrapper<>();
            targetStockWrapper.eq(InventoryStock::getWarehouseId, transfer.getTargetWarehouseId())
                    .eq(InventoryStock::getMaterialId, detail.getMaterialId())
                    .eq(InventoryStock::getBatchNo, detail.getBatchNo());
            InventoryStock targetStock = stockMapper.selectOne(targetStockWrapper);

            BigDecimal beforeTargetQuantity = targetStock != null ? targetStock.getQuantity() : BigDecimal.ZERO;
            BigDecimal afterTargetQuantity = beforeTargetQuantity.add(detail.getActualQuantity());

            if (targetStock == null) {
                targetStock = new InventoryStock();
                targetStock.setTenantId(getCurrentTenantId());
                targetStock.setWarehouseId(transfer.getTargetWarehouseId());
                targetStock.setMaterialId(detail.getMaterialId());
                targetStock.setQuantity(afterTargetQuantity);
                targetStock.setAvailableQuantity(afterTargetQuantity);
                targetStock.setLockedQuantity(BigDecimal.ZERO);
                targetStock.setInTransitQuantity(BigDecimal.ZERO);
                targetStock.setStockStatus("NORMAL");
                targetStock.setBatchNo(detail.getBatchNo());
                targetStock.setCreateTime(LocalDateTime.now());
                targetStock.setUpdateTime(LocalDateTime.now());
                stockMapper.insert(targetStock);
            } else {
                targetStock.setQuantity(afterTargetQuantity);
                targetStock.setAvailableQuantity(targetStock.getAvailableQuantity().add(detail.getActualQuantity()));
                targetStock.setUpdateTime(LocalDateTime.now());
                stockMapper.updateById(targetStock);
            }

            recordMapper.insert(createRecord("OUTBOUND", "STOCK_TRANSFER", transfer.getTransferNo(),
                    transfer.getSourceWarehouseId(), detail.getMaterialId(), detail.getBatchNo(),
                    beforeSourceQuantity, detail.getActualQuantity().negate(), afterSourceQuantity,
                    getCurrentUserId(), "System"));

            recordMapper.insert(createRecord("INBOUND", "STOCK_TRANSFER", transfer.getTransferNo(),
                    transfer.getTargetWarehouseId(), detail.getMaterialId(), detail.getBatchNo(),
                    beforeTargetQuantity, detail.getActualQuantity(), afterTargetQuantity,
                    getCurrentUserId(), "System"));
        }

        transfer.setTransferStatus("COMPLETED");
        transfer.setTransferDate(LocalDateTime.now());
        transfer.setUpdateTime(LocalDateTime.now());
        transferMapper.updateById(transfer);
    }

    @Override
    public TransferDTO getTransferById(Long id) {
        InventoryTransfer transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw new RuntimeException("调拨单不存在");
        }

        TransferDTO dto = convertToDTO(transfer);

        List<InventoryTransferDetail> details = transferDetailMapper.selectList(
                new LambdaQueryWrapper<InventoryTransferDetail>()
                        .eq(InventoryTransferDetail::getTransferId, id)
        );
        dto.setDetails(details.stream().map(this::convertDetailToDTO).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public Page<TransferDTO> queryTransfers(TransferQueryDTO dto) {
        IPage<InventoryTransfer> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<InventoryTransfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryTransfer::getTenantId, getCurrentTenantId())
                .like(dto.getTransferNo() != null, InventoryTransfer::getTransferNo, dto.getTransferNo())
                .eq(dto.getTransferType() != null, InventoryTransfer::getTransferType, dto.getTransferType())
                .eq(dto.getSourceWarehouseId() != null, InventoryTransfer::getSourceWarehouseId, dto.getSourceWarehouseId())
                .eq(dto.getTargetWarehouseId() != null, InventoryTransfer::getTargetWarehouseId, dto.getTargetWarehouseId())
                .eq(dto.getTransferStatus() != null, InventoryTransfer::getTransferStatus, dto.getTransferStatus())
                .ge(dto.getStartDate() != null, InventoryTransfer::getCreateTime, dto.getStartDate())
                .le(dto.getEndDate() != null, InventoryTransfer::getCreateTime, dto.getEndDate());
        IPage<InventoryTransfer> result = transferMapper.selectPage(page, wrapper);

        Page<TransferDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    private TransferDTO convertToDTO(InventoryTransfer entity) {
        TransferDTO dto = new TransferDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private TransferDetailDTO convertDetailToDTO(InventoryTransferDetail entity) {
        TransferDetailDTO dto = new TransferDetailDTO();
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

    private String generateTransferNo() {
        return "TRF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private Long getCurrentTenantId() {
        return 1L;
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
