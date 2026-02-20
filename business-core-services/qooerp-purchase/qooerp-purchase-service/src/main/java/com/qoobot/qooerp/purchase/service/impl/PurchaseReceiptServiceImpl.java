package com.qoobot.qooerp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.purchase.dto.ReceiptCreateDTO;
import com.qoobot.qooerp.purchase.dto.ReceiptDTO;
import com.qoobot.qooerp.purchase.dto.ReceiptDetailCreateDTO;
import com.qoobot.qooerp.purchase.dto.ReceiptDetailDTO;
import com.qoobot.qooerp.purchase.dto.ReceiptQueryDTO;
import com.qoobot.qooerp.purchase.entity.PurchaseOrder;
import com.qoobot.qooerp.purchase.entity.PurchaseOrderDetail;
import com.qoobot.qooerp.purchase.entity.PurchaseReceipt;
import com.qoobot.qooerp.purchase.entity.PurchaseReceiptDetail;
import com.qoobot.qooerp.purchase.enums.OrderStatus;
import com.qoobot.qooerp.purchase.enums.ReceiptStatus;
import com.qoobot.qooerp.purchase.mapper.PurchaseOrderDetailMapper;
import com.qoobot.qooerp.purchase.mapper.PurchaseOrderMapper;
import com.qoobot.qooerp.purchase.mapper.PurchaseReceiptDetailMapper;
import com.qoobot.qooerp.purchase.mapper.PurchaseReceiptMapper;
import com.qoobot.qooerp.purchase.service.PurchaseReceiptService;
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
public class PurchaseReceiptServiceImpl implements PurchaseReceiptService {

    private final PurchaseReceiptMapper receiptMapper;
    private final PurchaseReceiptDetailMapper receiptDetailMapper;
    private final PurchaseOrderMapper orderMapper;
    private final PurchaseOrderDetailMapper orderDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> createReceipt(ReceiptCreateDTO dto) {
        PurchaseOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }
        if (!OrderStatus.APPROVED.getCode().equals(order.getOrderStatus()) &&
            !OrderStatus.RECEIVING.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException(60004, "订单状态不正确，只有已审核或收货中状态可以创建入库单");
        }

        for (ReceiptDetailCreateDTO detailDTO : dto.getDetails()) {
            PurchaseOrderDetail orderDetail = orderDetailMapper.selectById(detailDTO.getOrderDetailId());
            if (orderDetail == null) {
                throw new BusinessException(60011, "订单明细不存在");
            }
            BigDecimal canReceive = orderDetail.getQuantity().subtract(orderDetail.getReceivedQuantity());
            if (detailDTO.getQuantity().compareTo(canReceive) > 0) {
                throw new BusinessException(60012, "入库数量超过订单未收货数量");
            }
        }

        String receiptCode = generateReceiptCode();

        PurchaseReceipt receipt = new PurchaseReceipt();
        receipt.setReceiptCode(receiptCode);
        receipt.setOrderId(dto.getOrderId());
        receipt.setOrderCode(order.getOrderCode());
        receipt.setSupplierId(order.getSupplierId());
        receipt.setSupplierName(order.getSupplierName());
        receipt.setReceiptDate(dto.getReceiptDate());
        receipt.setWarehouseId(dto.getWarehouseId());
        receipt.setWarehouseName(dto.getWarehouseName());
        receipt.setRemark(dto.getRemark());
        receipt.setReceiptStatus(ReceiptStatus.PENDING.getCode());

        BigDecimal receiptAmount = dto.getDetails().stream()
                .map(detailDTO -> {
                    PurchaseReceiptDetail detail = new PurchaseReceiptDetail();
                    detail.setReceiptId(null);
                    detail.setOrderDetailId(detailDTO.getOrderDetailId());
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
                    detail.setProductionDate(detailDTO.getProductionDate());
                    detail.setExpiryDate(detailDTO.getExpiryDate());
                    detail.setRemark(detailDTO.getRemark());
                    return detail;
                })
                .map(PurchaseReceiptDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        receipt.setReceiptAmount(receiptAmount);

        receiptMapper.insert(receipt);

        List<PurchaseReceiptDetail> details = dto.getDetails().stream().map(detailDTO -> {
            PurchaseReceiptDetail detail = new PurchaseReceiptDetail();
            detail.setReceiptId(null);
            detail.setOrderDetailId(detailDTO.getOrderDetailId());
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
            detail.setProductionDate(detailDTO.getProductionDate());
            detail.setExpiryDate(detailDTO.getExpiryDate());
            detail.setRemark(detailDTO.getRemark());
            return detail;
        }).collect(Collectors.toList());

        for (PurchaseReceiptDetail detail : details) {
            detail.setReceiptId(receipt.getId());
            receiptDetailMapper.insert(detail);
        }

        return Result.success(receipt.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteReceipt(Long id) {
        PurchaseReceipt receipt = receiptMapper.selectById(id);
        if (receipt == null) {
            throw new BusinessException(60006, "入库单不存在");
        }
        if (!ReceiptStatus.PENDING.getCode().equals(receipt.getReceiptStatus())) {
            throw new BusinessException(60007, "入库单已审核，不能删除");
        }

        LambdaQueryWrapper<PurchaseReceiptDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseReceiptDetail::getReceiptId, id);
        receiptDetailMapper.delete(queryWrapper);

        receiptMapper.deleteById(id);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveReceipt(Long id) {
        PurchaseReceipt receipt = receiptMapper.selectById(id);
        if (receipt == null) {
            throw new BusinessException(60006, "入库单不存在");
        }
        if (!ReceiptStatus.PENDING.getCode().equals(receipt.getReceiptStatus())) {
            throw new BusinessException(60007, "入库单已审核");
        }

        receipt.setReceiptStatus(ReceiptStatus.COMPLETED.getCode());
        receiptMapper.updateById(receipt);

        LambdaQueryWrapper<PurchaseReceiptDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
        detailQueryWrapper.eq(PurchaseReceiptDetail::getReceiptId, id);
        List<PurchaseReceiptDetail> receiptDetails = receiptDetailMapper.selectList(detailQueryWrapper);

        for (PurchaseReceiptDetail receiptDetail : receiptDetails) {
            PurchaseOrderDetail orderDetail = orderDetailMapper.selectById(receiptDetail.getOrderDetailId());
            if (orderDetail != null) {
                BigDecimal newReceived = orderDetail.getReceivedQuantity().add(receiptDetail.getQuantity());
                orderDetail.setReceivedQuantity(newReceived);
                orderDetailMapper.updateById(orderDetail);
            }
        }

        PurchaseOrder order = orderMapper.selectById(receipt.getOrderId());
        if (order != null && OrderStatus.APPROVED.getCode().equals(order.getOrderStatus())) {
            order.setOrderStatus(OrderStatus.RECEIVING.getCode());
            orderMapper.updateById(order);
        }

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelReceipt(Long id) {
        PurchaseReceipt receipt = receiptMapper.selectById(id);
        if (receipt == null) {
            throw new BusinessException(60006, "入库单不存在");
        }
        if (!ReceiptStatus.PENDING.getCode().equals(receipt.getReceiptStatus())) {
            throw new BusinessException(60007, "入库单已审核，不能取消");
        }

        receipt.setReceiptStatus(ReceiptStatus.CANCELLED.getCode());
        receiptMapper.updateById(receipt);

        return Result.success();
    }

    @Override
    public Result<ReceiptDTO> getReceipt(Long id) {
        PurchaseReceipt receipt = receiptMapper.selectById(id);
        if (receipt == null) {
            throw new BusinessException(60006, "入库单不存在");
        }

        ReceiptDTO receiptDTO = convertToDTO(receipt);

        LambdaQueryWrapper<PurchaseReceiptDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseReceiptDetail::getReceiptId, id);
        List<PurchaseReceiptDetail> details = receiptDetailMapper.selectList(queryWrapper);
        List<ReceiptDetailDTO> detailDTOs = details.stream()
                .map(this::convertDetailToDTO)
                .collect(Collectors.toList());
        receiptDTO.setDetails(detailDTOs);

        return Result.success(receiptDTO);
    }

    @Override
    public Result<PageResult<ReceiptDTO>> queryPage(ReceiptQueryDTO dto) {
        Page<PurchaseReceipt> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        java.time.LocalDate startDate = null;
        java.time.LocalDate endDate = null;
        if (dto.getStartDate() != null) {
            startDate = java.time.LocalDate.parse(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            endDate = java.time.LocalDate.parse(dto.getEndDate());
        }

        IPage<PurchaseReceipt> result = receiptMapper.queryPage(page, dto.getReceiptCode(),
                dto.getOrderId(), dto.getSupplierId(), dto.getReceiptStatus(), startDate, endDate);

        List<ReceiptDTO> records = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResult<ReceiptDTO> pageResult = PageResult.of(
                result.getCurrent(), result.getSize(), result.getTotal(), records);

        return Result.success(pageResult);
    }

    private String generateReceiptCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PR" + date;
        LambdaQueryWrapper<PurchaseReceipt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.likeRight(PurchaseReceipt::getReceiptCode, prefix);
        queryWrapper.orderByDesc(PurchaseReceipt::getReceiptCode);
        queryWrapper.last("LIMIT 1");
        PurchaseReceipt lastReceipt = receiptMapper.selectOne(queryWrapper);
        int sequence = 1;
        if (lastReceipt != null) {
            String lastCode = lastReceipt.getReceiptCode();
            String lastSeq = lastCode.substring(prefix.length());
            sequence = Integer.parseInt(lastSeq) + 1;
        }
        return prefix + String.format("%04d", sequence);
    }

    private ReceiptDTO convertToDTO(PurchaseReceipt receipt) {
        ReceiptDTO dto = new ReceiptDTO();
        BeanUtils.copyProperties(receipt, dto);
        dto.setReceiptDate(receipt.getReceiptDate().toString());
        for (ReceiptStatus status : ReceiptStatus.values()) {
            if (status.getCode().equals(receipt.getReceiptStatus())) {
                dto.setReceiptStatusName(status.getDesc());
                break;
            }
        }
        return dto;
    }

    private ReceiptDetailDTO convertDetailToDTO(PurchaseReceiptDetail detail) {
        ReceiptDetailDTO dto = new ReceiptDetailDTO();
        BeanUtils.copyProperties(detail, dto);
        return dto;
    }
}
