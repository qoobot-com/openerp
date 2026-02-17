package com.qoobot.qooerp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.*;
import com.qoobot.qooerp.inventory.entity.*;
import com.qoobot.qooerp.inventory.mapper.*;
import com.qoobot.qooerp.inventory.service.InventoryCheckService;
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
public class InventoryCheckServiceImpl implements InventoryCheckService {

    private final InventoryCheckMapper checkMapper;
    private final InventoryCheckDetailMapper checkDetailMapper;
    private final InventoryStockMapper stockMapper;
    private final InventoryStockRecordMapper recordMapper;
    private final InventoryMaterialMapper materialMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCheck(CheckCreateDTO dto) {
        String checkNo = generateCheckNo();

        InventoryCheck check = new InventoryCheck();
        check.setTenantId(getCurrentTenantId());
        check.setCheckNo(checkNo);
        check.setCheckType(dto.getCheckType());
        check.setWarehouseId(dto.getWarehouseId());
        check.setCheckStatus("DRAFT");
        check.setTotalItems(dto.getDetails().size());
        check.setDiffItems(0);
        check.setOperatorId(getCurrentUserId());
        check.setOperatorName("System");
        check.setCreateBy(getCurrentUserId());
        check.setCreateTime(LocalDateTime.now());
        checkMapper.insert(check);

        for (CheckDetailCreateDTO detailDTO : dto.getDetails()) {
            InventoryMaterial material = materialMapper.selectById(detailDTO.getMaterialId());
            if (material == null) {
                throw new RuntimeException("物料不存在");
            }

            LambdaQueryWrapper<InventoryStock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(InventoryStock::getWarehouseId, dto.getWarehouseId())
                    .eq(InventoryStock::getMaterialId, detailDTO.getMaterialId())
                    .eq(InventoryStock::getBatchNo, detailDTO.getBatchNo());
            InventoryStock stock = stockMapper.selectOne(stockWrapper);
            BigDecimal systemQuantity = stock != null ? stock.getQuantity() : BigDecimal.ZERO;

            InventoryCheckDetail detail = new InventoryCheckDetail();
            detail.setTenantId(getCurrentTenantId());
            detail.setCheckId(check.getId());
            detail.setCheckNo(checkNo);
            detail.setMaterialId(detailDTO.getMaterialId());
            detail.setMaterialCode(material.getMaterialCode());
            detail.setMaterialName(material.getMaterialName());
            detail.setMaterialSpec(material.getMaterialSpec());
            detail.setMaterialUnit(material.getMaterialUnit());
            detail.setSystemQuantity(systemQuantity);
            detail.setActualQuantity(detailDTO.getActualQuantity());
            detail.setDiffQuantity(detailDTO.getActualQuantity().subtract(systemQuantity));
            detail.setBatchNo(detailDTO.getBatchNo());
            detail.setCreateTime(LocalDateTime.now());
            checkDetailMapper.insert(detail);

            if (detail.getDiffQuantity().compareTo(BigDecimal.ZERO) != 0) {
                check.setDiffItems(check.getDiffItems() + 1);
            }
        }

        checkMapper.updateById(check);

        return check.getId();
    }

    @Override
    public void submitCheck(Long id) {
        InventoryCheck check = checkMapper.selectById(id);
        if (check == null) {
            throw new RuntimeException("盘点单不存在");
        }
        check.setCheckStatus("PENDING");
        check.setUpdateBy(getCurrentUserId());
        check.setUpdateTime(LocalDateTime.now());
        checkMapper.updateById(check);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditCheck(Long id, Boolean approved) {
        InventoryCheck check = checkMapper.selectById(id);
        if (check == null) {
            throw new RuntimeException("盘点单不存在");
        }

        if (approved) {
            check.setCheckStatus("APPROVED");
            check.setAuditorId(getCurrentUserId());
            check.setAuditorName("System");
            check.setAuditTime(LocalDateTime.now());
            check.setUpdateTime(LocalDateTime.now());
            checkMapper.updateById(check);
        } else {
            check.setCheckStatus("REJECTED");
            check.setAuditorId(getCurrentUserId());
            check.setAuditTime(LocalDateTime.now());
            check.setUpdateTime(LocalDateTime.now());
            checkMapper.updateById(check);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeCheck(Long id) {
        InventoryCheck check = checkMapper.selectById(id);
        if (check == null) {
            throw new RuntimeException("盘点单不存在");
        }

        if (!"APPROVED".equals(check.getCheckStatus())) {
            throw new RuntimeException("盘点单未审核通过");
        }

        List<InventoryCheckDetail> details = checkDetailMapper.selectList(
                new LambdaQueryWrapper<InventoryCheckDetail>()
                        .eq(InventoryCheckDetail::getCheckId, id)
        );

        for (InventoryCheckDetail detail : details) {
            if (detail.getDiffQuantity().compareTo(BigDecimal.ZERO) != 0) {
                LambdaQueryWrapper<InventoryStock> stockWrapper = new LambdaQueryWrapper<>();
                stockWrapper.eq(InventoryStock::getWarehouseId, check.getWarehouseId())
                        .eq(InventoryStock::getMaterialId, detail.getMaterialId())
                        .eq(InventoryStock::getBatchNo, detail.getBatchNo());
                InventoryStock stock = stockMapper.selectOne(stockWrapper);

                BigDecimal beforeQuantity = stock != null ? stock.getQuantity() : BigDecimal.ZERO;
                BigDecimal afterQuantity = beforeQuantity.add(detail.getDiffQuantity());

                if (stock == null) {
                    stock = new InventoryStock();
                    stock.setTenantId(getCurrentTenantId());
                    stock.setWarehouseId(check.getWarehouseId());
                    stock.setMaterialId(detail.getMaterialId());
                    stock.setQuantity(afterQuantity);
                    stock.setAvailableQuantity(afterQuantity);
                    stock.setLockedQuantity(BigDecimal.ZERO);
                    stock.setInTransitQuantity(BigDecimal.ZERO);
                    stock.setStockStatus("NORMAL");
                    stock.setBatchNo(detail.getBatchNo());
                    stock.setCreateTime(LocalDateTime.now());
                    stock.setUpdateTime(LocalDateTime.now());
                    stockMapper.insert(stock);
                } else {
                    stock.setQuantity(afterQuantity);
                    stock.setAvailableQuantity(stock.getAvailableQuantity().add(detail.getDiffQuantity()));
                    stock.setUpdateTime(LocalDateTime.now());
                    stockMapper.updateById(stock);
                }

                recordMapper.insert(createRecord("ADJUST", "STOCK_CHECK", check.getCheckNo(),
                        check.getWarehouseId(), detail.getMaterialId(), detail.getBatchNo(),
                        beforeQuantity, detail.getDiffQuantity(), afterQuantity,
                        getCurrentUserId(), "System"));
            }
        }

        check.setCheckStatus("COMPLETED");
        check.setCheckDate(LocalDateTime.now());
        check.setUpdateTime(LocalDateTime.now());
        checkMapper.updateById(check);
    }

    @Override
    public CheckDTO getCheckById(Long id) {
        InventoryCheck check = checkMapper.selectById(id);
        if (check == null) {
            throw new RuntimeException("盘点单不存在");
        }

        CheckDTO dto = convertToDTO(check);

        List<InventoryCheckDetail> details = checkDetailMapper.selectList(
                new LambdaQueryWrapper<InventoryCheckDetail>()
                        .eq(InventoryCheckDetail::getCheckId, id)
        );
        dto.setDetails(details.stream().map(this::convertDetailToDTO).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public Page<CheckDTO> queryChecks(CheckQueryDTO dto) {
        IPage<InventoryCheck> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<InventoryCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryCheck::getTenantId, getCurrentTenantId())
                .like(dto.getCheckNo() != null, InventoryCheck::getCheckNo, dto.getCheckNo())
                .eq(dto.getCheckType() != null, InventoryCheck::getCheckType, dto.getCheckType())
                .eq(dto.getWarehouseId() != null, InventoryCheck::getWarehouseId, dto.getWarehouseId())
                .eq(dto.getCheckStatus() != null, InventoryCheck::getCheckStatus, dto.getCheckStatus())
                .ge(dto.getStartDate() != null, InventoryCheck::getCreateTime, dto.getStartDate())
                .le(dto.getEndDate() != null, InventoryCheck::getCreateTime, dto.getEndDate());
        IPage<InventoryCheck> result = checkMapper.selectPage(page, wrapper);

        Page<CheckDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    private CheckDTO convertToDTO(InventoryCheck entity) {
        CheckDTO dto = new CheckDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private CheckDetailDTO convertDetailToDTO(InventoryCheckDetail entity) {
        CheckDetailDTO dto = new CheckDetailDTO();
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

    private String generateCheckNo() {
        return "CHK" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private Long getCurrentTenantId() {
        return 1L;
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
