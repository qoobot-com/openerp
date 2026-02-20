package com.qoobot.qooerp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.enums.ResponseCode;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.purchase.dto.OrderCreateDTO;
import com.qoobot.qooerp.purchase.dto.OrderDTO;
import com.qoobot.qooerp.purchase.dto.OrderDetailCreateDTO;
import com.qoobot.qooerp.purchase.dto.OrderDetailDTO;
import com.qoobot.qooerp.purchase.dto.OrderQueryDTO;
import com.qoobot.qooerp.purchase.dto.OrderUpdateDTO;
import com.qoobot.qooerp.purchase.dto.RejectReasonDTO;
import com.qoobot.qooerp.purchase.entity.PurchaseOrder;
import com.qoobot.qooerp.purchase.entity.PurchaseOrderDetail;
import com.qoobot.qooerp.purchase.enums.OrderStatus;
import com.qoobot.qooerp.purchase.mapper.PurchaseOrderDetailMapper;
import com.qoobot.qooerp.purchase.mapper.PurchaseOrderMapper;
import com.qoobot.qooerp.purchase.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderMapper orderMapper;
    private final PurchaseOrderDetailMapper orderDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> createOrder(OrderCreateDTO dto) {
        String orderCode = generateOrderCode();

        PurchaseOrder order = new PurchaseOrder();
        order.setOrderCode(orderCode);
        order.setSupplierId(dto.getSupplierId());
        order.setSupplierName(dto.getSupplierName());
        order.setOrderDate(dto.getOrderDate());
        order.setDeliveryDate(dto.getDeliveryDate());
        order.setWarehouseId(dto.getWarehouseId());
        order.setWarehouseName(dto.getWarehouseName());
        order.setRemark(dto.getRemark());
        order.setOrderStatus(OrderStatus.DRAFT.getCode());

        BigDecimal orderAmount = dto.getDetails().stream()
                .map(detailDTO -> {
                    PurchaseOrderDetail detail = new PurchaseOrderDetail();
                    detail.setOrderId(null);
                    detail.setMaterialId(detailDTO.getMaterialId());
                    detail.setMaterialCode(detailDTO.getMaterialCode());
                    detail.setMaterialName(detailDTO.getMaterialName());
                    detail.setSpecification(detailDTO.getSpecification());
                    detail.setUnit(detailDTO.getUnit());
                    detail.setQuantity(detailDTO.getQuantity());
                    detail.setUnitPrice(detailDTO.getUnitPrice());
                    BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
                    detail.setAmount(amount);
                    detail.setDiscountRate(detailDTO.getDiscountRate());
                    BigDecimal discAmt = amount.multiply(detailDTO.getDiscountRate().divide(BigDecimal.valueOf(100)));
                    detail.setDiscountAmount(discAmt);
                    BigDecimal payableAmt = amount.subtract(discAmt);
                    detail.setPayableAmount(payableAmt);
                    detail.setReceivedQuantity(BigDecimal.ZERO);
                    detail.setRemark(detailDTO.getRemark());
                    return detail;
                })
                .map(PurchaseOrderDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountAmount = dto.getDetails().stream()
                .map(detailDTO -> {
                    PurchaseOrderDetail detail = new PurchaseOrderDetail();
                    detail.setMaterialId(detailDTO.getMaterialId());
                    detail.setUnitPrice(detailDTO.getUnitPrice());
                    BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
                    detail.setDiscountRate(detailDTO.getDiscountRate());
                    return amount.multiply(detailDTO.getDiscountRate().divide(BigDecimal.valueOf(100)));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setOrderAmount(orderAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayableAmount(orderAmount.subtract(discountAmount));

        orderMapper.insert(order);

        List<PurchaseOrderDetail> details = dto.getDetails().stream().map(detailDTO -> {
            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setOrderId(null);
            detail.setMaterialId(detailDTO.getMaterialId());
            detail.setMaterialCode(detailDTO.getMaterialCode());
            detail.setMaterialName(detailDTO.getMaterialName());
            detail.setSpecification(detailDTO.getSpecification());
            detail.setUnit(detailDTO.getUnit());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(detailDTO.getUnitPrice());
            BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
            detail.setAmount(amount);
            detail.setDiscountRate(detailDTO.getDiscountRate());
            BigDecimal discAmt = amount.multiply(detailDTO.getDiscountRate().divide(BigDecimal.valueOf(100)));
            detail.setDiscountAmount(discAmt);
            BigDecimal payableAmt = amount.subtract(discAmt);
            detail.setPayableAmount(payableAmt);
            detail.setReceivedQuantity(BigDecimal.ZERO);
            detail.setRemark(detailDTO.getRemark());
            return detail;
        }).collect(Collectors.toList());

        for (PurchaseOrderDetail detail : details) {
            detail.setOrderId(order.getId());
            orderDetailMapper.insert(detail);
        }

        return Result.success(order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateOrder(OrderUpdateDTO dto) {
        PurchaseOrder order = orderMapper.selectById(dto.getId());
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }
        if (!OrderStatus.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException(60004, "订单状态不正确，只有草稿状态可以编辑");
        }

        if (dto.getDeliveryDate() != null) {
            order.setDeliveryDate(dto.getDeliveryDate());
        }
        if (dto.getWarehouseId() != null) {
            order.setWarehouseId(dto.getWarehouseId());
        }
        if (dto.getWarehouseName() != null) {
            order.setWarehouseName(dto.getWarehouseName());
        }
        if (dto.getRemark() != null) {
            order.setRemark(dto.getRemark());
        }

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            LambdaQueryWrapper<PurchaseOrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PurchaseOrderDetail::getOrderId, order.getId());
            orderDetailMapper.delete(queryWrapper);

            BigDecimal orderAmount = dto.getDetails().stream()
                    .map(detailDTO -> {
                        PurchaseOrderDetail detail = new PurchaseOrderDetail();
                        detail.setOrderId(order.getId());
                        detail.setMaterialId(detailDTO.getMaterialId());
                        detail.setMaterialCode(detailDTO.getMaterialCode());
                        detail.setMaterialName(detailDTO.getMaterialName());
                        detail.setSpecification(detailDTO.getSpecification());
                        detail.setUnit(detailDTO.getUnit());
                        detail.setQuantity(detailDTO.getQuantity());
                        detail.setUnitPrice(detailDTO.getUnitPrice());
                        BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
                        detail.setAmount(amount);
                        detail.setDiscountRate(detailDTO.getDiscountRate());
                        BigDecimal discAmt = amount.multiply(detailDTO.getDiscountRate().divide(BigDecimal.valueOf(100)));
                        detail.setDiscountAmount(discAmt);
                        BigDecimal payableAmt = amount.subtract(discAmt);
                        detail.setPayableAmount(payableAmt);
                        detail.setReceivedQuantity(BigDecimal.ZERO);
                        detail.setRemark(detailDTO.getRemark());
                        return detail;
                    })
                    .map(PurchaseOrderDetail::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal discountAmount = dto.getDetails().stream()
                    .map(detailDTO -> {
                        PurchaseOrderDetail detail = new PurchaseOrderDetail();
                        detail.setMaterialId(detailDTO.getMaterialId());
                        detail.setUnitPrice(detailDTO.getUnitPrice());
                        BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
                        detail.setDiscountRate(detailDTO.getDiscountRate());
                        return amount.multiply(detailDTO.getDiscountRate().divide(BigDecimal.valueOf(100)));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setOrderAmount(orderAmount);
            order.setDiscountAmount(discountAmount);
            order.setPayableAmount(orderAmount.subtract(discountAmount));

            List<PurchaseOrderDetail> details = dto.getDetails().stream().map(detailDTO -> {
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setOrderId(order.getId());
                detail.setMaterialId(detailDTO.getMaterialId());
                detail.setMaterialCode(detailDTO.getMaterialCode());
                detail.setMaterialName(detailDTO.getMaterialName());
                detail.setSpecification(detailDTO.getSpecification());
                detail.setUnit(detailDTO.getUnit());
                detail.setQuantity(detailDTO.getQuantity());
                detail.setUnitPrice(detailDTO.getUnitPrice());
                BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
                detail.setAmount(amount);
                detail.setDiscountRate(detailDTO.getDiscountRate());
                BigDecimal discAmt = amount.multiply(detailDTO.getDiscountRate().divide(BigDecimal.valueOf(100)));
                detail.setDiscountAmount(discAmt);
                BigDecimal payableAmt = amount.subtract(discAmt);
                detail.setPayableAmount(payableAmt);
                detail.setReceivedQuantity(BigDecimal.ZERO);
                detail.setRemark(detailDTO.getRemark());
                return detail;
            }).collect(Collectors.toList());

            for (PurchaseOrderDetail detail : details) {
                orderDetailMapper.insert(detail);
            }
        }

        orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteOrder(Long id) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }
        if (!OrderStatus.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException(60004, "订单状态不正确，只有草稿状态可以删除");
        }

        LambdaQueryWrapper<PurchaseOrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseOrderDetail::getOrderId, id);
        orderDetailMapper.delete(queryWrapper);

        orderMapper.deleteById(id);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> submitOrder(Long id) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }
        if (!OrderStatus.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException(60004, "订单状态不正确，只有草稿状态可以提交审核");
        }

        order.setOrderStatus(OrderStatus.PENDING.getCode());
        orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveOrder(Long id) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }
        if (!OrderStatus.PENDING.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException(60004, "订单状态不正确，只有待审核状态可以审核");
        }

        order.setOrderStatus(OrderStatus.APPROVED.getCode());
        orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> rejectOrder(Long id, RejectReasonDTO dto) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }
        if (!OrderStatus.PENDING.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException(60004, "订单状态不正确，只有待审核状态可以驳回");
        }

        order.setOrderStatus(OrderStatus.DRAFT.getCode());
        String remark = StringUtils.hasText(order.getRemark()) ? order.getRemark() : "";
        order.setRemark(remark + " [驳回原因: " + dto.getReason() + "]");
        orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelOrder(Long id) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }
        if (OrderStatus.CANCELLED.getCode().equals(order.getOrderStatus()) ||
            OrderStatus.COMPLETED.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException(60004, "订单状态不正确，不能取消");
        }

        order.setOrderStatus(OrderStatus.CANCELLED.getCode());
        orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> completeOrder(Long id) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }
        if (!OrderStatus.RECEIVING.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException(60004, "订单状态不正确，只有收货中状态可以完成");
        }

        order.setOrderStatus(OrderStatus.COMPLETED.getCode());
        orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    public Result<OrderDTO> getOrder(Long id) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(60005, "订单不存在");
        }

        OrderDTO orderDTO = convertToDTO(order);

        LambdaQueryWrapper<PurchaseOrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseOrderDetail::getOrderId, id);
        List<PurchaseOrderDetail> details = orderDetailMapper.selectList(queryWrapper);
        List<OrderDetailDTO> detailDTOs = details.stream()
                .map(this::convertDetailToDTO)
                .collect(Collectors.toList());
        orderDTO.setDetails(detailDTOs);

        return Result.success(orderDTO);
    }

    @Override
    public Result<PageResult<OrderDTO>> queryPage(OrderQueryDTO dto) {
        Page<PurchaseOrder> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LocalDate startDate = null;
        LocalDate endDate = null;
        if (StringUtils.hasText(dto.getStartDate())) {
            startDate = LocalDate.parse(dto.getStartDate());
        }
        if (StringUtils.hasText(dto.getEndDate())) {
            endDate = LocalDate.parse(dto.getEndDate());
        }

        IPage<PurchaseOrder> result = orderMapper.queryPage(page, dto.getOrderCode(),
                dto.getSupplierId(), dto.getOrderStatus(), startDate, endDate);

        List<OrderDTO> records = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResult<OrderDTO> pageResult = PageResult.of(
                result.getCurrent(), result.getSize(), result.getTotal(), records);

        return Result.success(pageResult);
    }

    private String generateOrderCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PO" + date;
        LambdaQueryWrapper<PurchaseOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.likeRight(PurchaseOrder::getOrderCode, prefix);
        queryWrapper.orderByDesc(PurchaseOrder::getOrderCode);
        queryWrapper.last("LIMIT 1");
        PurchaseOrder lastOrder = orderMapper.selectOne(queryWrapper);
        int sequence = 1;
        if (lastOrder != null) {
            String lastCode = lastOrder.getOrderCode();
            String lastSeq = lastCode.substring(prefix.length());
            sequence = Integer.parseInt(lastSeq) + 1;
        }
        return prefix + String.format("%04d", sequence);
    }

    private OrderDTO convertToDTO(PurchaseOrder order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setOrderDate(order.getOrderDate().toString());
        dto.setDeliveryDate(order.getDeliveryDate().toString());
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(order.getOrderStatus())) {
                dto.setOrderStatusName(status.getDesc());
                break;
            }
        }
        return dto;
    }

    private OrderDetailDTO convertDetailToDTO(PurchaseOrderDetail detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        BeanUtils.copyProperties(detail, dto);
        return dto;
    }
}
