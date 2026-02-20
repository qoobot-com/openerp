package com.qoobot.qooerp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.purchase.dto.ReturnCreateDTO;
import com.qoobot.qooerp.purchase.dto.ReturnDTO;
import com.qoobot.qooerp.purchase.dto.ReturnDetailCreateDTO;
import com.qoobot.qooerp.purchase.dto.ReturnDetailDTO;
import com.qoobot.qooerp.purchase.dto.ReturnQueryDTO;
import com.qoobot.qooerp.purchase.entity.PurchaseOrder;
import com.qoobot.qooerp.purchase.entity.PurchaseReturn;
import com.qoobot.qooerp.purchase.entity.PurchaseReturnDetail;
import com.qoobot.qooerp.purchase.enums.ReturnStatus;
import com.qoobot.qooerp.purchase.mapper.PurchaseOrderMapper;
import com.qoobot.qooerp.purchase.mapper.PurchaseReturnDetailMapper;
import com.qoobot.qooerp.purchase.mapper.PurchaseReturnMapper;
import com.qoobot.qooerp.purchase.service.PurchaseReturnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseReturnServiceImpl implements PurchaseReturnService {

    private final PurchaseReturnMapper returnMapper;
    private final PurchaseReturnDetailMapper returnDetailMapper;
    private final PurchaseOrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> createReturn(ReturnCreateDTO dto) {
        PurchaseOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }

        String returnCode = generateReturnCode();

        PurchaseReturn purchaseReturn = new PurchaseReturn();
        purchaseReturn.setReturnCode(returnCode);
        purchaseReturn.setOrderId(dto.getOrderId());
        purchaseReturn.setOrderCode(order.getOrderCode());
        purchaseReturn.setReceiptId(dto.getReceiptId());
        purchaseReturn.setSupplierId(order.getSupplierId());
        purchaseReturn.setSupplierName(order.getSupplierName());
        purchaseReturn.setReturnDate(dto.getReturnDate());
        purchaseReturn.setReason(dto.getReason());
        purchaseReturn.setWarehouseId(dto.getWarehouseId());
        purchaseReturn.setWarehouseName(dto.getWarehouseName());
        purchaseReturn.setRemark(dto.getRemark());
        purchaseReturn.setReturnStatus(ReturnStatus.PENDING.getCode());

        BigDecimal returnAmount = dto.getDetails().stream()
                .map(detailDTO -> {
                    PurchaseReturnDetail detail = new PurchaseReturnDetail();
                    detail.setReturnId(null);
                    detail.setMaterialId(detailDTO.getMaterialId());
                    detail.setMaterialCode(detailDTO.getMaterialCode());
                    detail.setMaterialName(detailDTO.getMaterialName());
                    detail.setSpecification(detailDTO.getSpecification());
                    detail.setUnit(detailDTO.getUnit());
                    detail.setQuantity(detailDTO.getQuantity());
                    detail.setUnitPrice(detailDTO.getUnitPrice());
                    BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
                    detail.setAmount(amount);
                    detail.setBatchNo(detailDTO.getBatchNo());
                    detail.setReason(detailDTO.getReason());
                    detail.setRemark(detailDTO.getRemark());
                    return detail;
                })
                .map(PurchaseReturnDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        purchaseReturn.setReturnAmount(returnAmount);

        returnMapper.insert(purchaseReturn);

        List<PurchaseReturnDetail> details = dto.getDetails().stream().map(detailDTO -> {
            PurchaseReturnDetail detail = new PurchaseReturnDetail();
            detail.setReturnId(null);
            detail.setMaterialId(detailDTO.getMaterialId());
            detail.setMaterialCode(detailDTO.getMaterialCode());
            detail.setMaterialName(detailDTO.getMaterialName());
            detail.setSpecification(detailDTO.getSpecification());
            detail.setUnit(detailDTO.getUnit());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(detailDTO.getUnitPrice());
            BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
            detail.setAmount(amount);
            detail.setBatchNo(detailDTO.getBatchNo());
            detail.setReason(detailDTO.getReason());
            detail.setRemark(detailDTO.getRemark());
            return detail;
        }).collect(Collectors.toList());

        for (PurchaseReturnDetail detail : details) {
            detail.setReturnId(purchaseReturn.getId());
            returnDetailMapper.insert(detail);
        }

        return Result.success(purchaseReturn.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteReturn(Long id) {
        PurchaseReturn purchaseReturn = returnMapper.selectById(id);
        if (purchaseReturn == null) {
            throw new BusinessException(60008, "退货单不存在");
        }
        if (!ReturnStatus.PENDING.getCode().equals(purchaseReturn.getReturnStatus())) {
            throw new BusinessException(60009, "退货单已审核，不能删除");
        }

        LambdaQueryWrapper<PurchaseReturnDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseReturnDetail::getReturnId, id);
        returnDetailMapper.delete(queryWrapper);

        returnMapper.deleteById(id);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveReturn(Long id) {
        PurchaseReturn purchaseReturn = returnMapper.selectById(id);
        if (purchaseReturn == null) {
            throw new BusinessException(60008, "退货单不存在");
        }
        if (!ReturnStatus.PENDING.getCode().equals(purchaseReturn.getReturnStatus())) {
            throw new BusinessException(60009, "退货单已审核");
        }

        purchaseReturn.setReturnStatus(ReturnStatus.APPROVED.getCode());
        returnMapper.updateById(purchaseReturn);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> completeReturn(Long id) {
        PurchaseReturn purchaseReturn = returnMapper.selectById(id);
        if (purchaseReturn == null) {
            throw new BusinessException(60008, "退货单不存在");
        }
        if (!ReturnStatus.APPROVED.getCode().equals(purchaseReturn.getReturnStatus())) {
            throw new BusinessException(60009, "退货单状态不正确，只有已审核状态可以完成");
        }

        purchaseReturn.setReturnStatus(ReturnStatus.COMPLETED.getCode());
        returnMapper.updateById(purchaseReturn);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelReturn(Long id) {
        PurchaseReturn purchaseReturn = returnMapper.selectById(id);
        if (purchaseReturn == null) {
            throw new BusinessException(60008, "退货单不存在");
        }
        if (ReturnStatus.CANCELLED.getCode().equals(purchaseReturn.getReturnStatus()) ||
            ReturnStatus.COMPLETED.getCode().equals(purchaseReturn.getReturnStatus())) {
            throw new BusinessException(60009, "退货单状态不正确，不能取消");
        }

        purchaseReturn.setReturnStatus(ReturnStatus.CANCELLED.getCode());
        returnMapper.updateById(purchaseReturn);

        return Result.success();
    }

    @Override
    public Result<ReturnDTO> getReturn(Long id) {
        PurchaseReturn purchaseReturn = returnMapper.selectById(id);
        if (purchaseReturn == null) {
            throw new BusinessException(60008, "退货单不存在");
        }

        ReturnDTO returnDTO = convertToDTO(purchaseReturn);

        LambdaQueryWrapper<PurchaseReturnDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseReturnDetail::getReturnId, id);
        List<PurchaseReturnDetail> details = returnDetailMapper.selectList(queryWrapper);
        List<ReturnDetailDTO> detailDTOs = details.stream()
                .map(this::convertDetailToDTO)
                .collect(Collectors.toList());
        returnDTO.setDetails(detailDTOs);

        return Result.success(returnDTO);
    }

    @Override
    public Result<PageResult<ReturnDTO>> queryPage(ReturnQueryDTO dto) {
        Page<PurchaseReturn> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        java.time.LocalDate startDate = null;
        java.time.LocalDate endDate = null;
        if (dto.getStartDate() != null) {
            startDate = java.time.LocalDate.parse(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            endDate = java.time.LocalDate.parse(dto.getEndDate());
        }

        IPage<PurchaseReturn> result = returnMapper.queryPage(page, dto.getReturnCode(),
                dto.getOrderId(), dto.getSupplierId(), dto.getReturnStatus(), startDate, endDate);

        List<ReturnDTO> records = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResult<ReturnDTO> pageResult = PageResult.of(
                result.getCurrent(), result.getSize(), result.getTotal(), records);

        return Result.success(pageResult);
    }

    private String generateReturnCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PRT" + date;
        LambdaQueryWrapper<PurchaseReturn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.likeRight(PurchaseReturn::getReturnCode, prefix);
        queryWrapper.orderByDesc(PurchaseReturn::getReturnCode);
        queryWrapper.last("LIMIT 1");
        PurchaseReturn lastReturn = returnMapper.selectOne(queryWrapper);
        int sequence = 1;
        if (lastReturn != null) {
            String lastCode = lastReturn.getReturnCode();
            String lastSeq = lastCode.substring(prefix.length());
            sequence = Integer.parseInt(lastSeq) + 1;
        }
        return prefix + String.format("%04d", sequence);
    }

    private ReturnDTO convertToDTO(PurchaseReturn purchaseReturn) {
        ReturnDTO dto = new ReturnDTO();
        BeanUtils.copyProperties(purchaseReturn, dto);
        dto.setReturnDate(purchaseReturn.getReturnDate().toString());
        for (ReturnStatus status : ReturnStatus.values()) {
            if (status.getCode().equals(purchaseReturn.getReturnStatus())) {
                dto.setReturnStatusName(status.getDesc());
                break;
            }
        }
        return dto;
    }

    private ReturnDetailDTO convertDetailToDTO(PurchaseReturnDetail detail) {
        ReturnDetailDTO dto = new ReturnDetailDTO();
        BeanUtils.copyProperties(detail, dto);
        return dto;
    }
}
