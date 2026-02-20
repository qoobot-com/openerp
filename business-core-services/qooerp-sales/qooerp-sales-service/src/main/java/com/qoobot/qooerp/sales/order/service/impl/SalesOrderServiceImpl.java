package com.qoobot.qooerp.sales.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.sales.order.domain.SalesOrder;
import com.qoobot.qooerp.sales.order.domain.SalesOrderDetail;
import com.qoobot.qooerp.sales.order.dto.OrderDTO;
import com.qoobot.qooerp.sales.order.dto.OrderDetailDTO;
import com.qoobot.qooerp.sales.order.dto.OrderQueryDTO;
import com.qoobot.qooerp.sales.order.mapper.SalesOrderDetailMapper;
import com.qoobot.qooerp.sales.order.mapper.SalesOrderMapper;
import com.qoobot.qooerp.sales.order.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 销售订单服务实现
 */
@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl extends ServiceImpl<SalesOrderMapper, SalesOrder>
        implements SalesOrderService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();
    private final SalesOrderDetailMapper orderDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> createOrder(OrderDTO orderDTO) {
        // 生成订单编号
        String orderCode = generateOrderCode();

        // 创建订单主表
        SalesOrder order = new SalesOrder();
        BeanUtils.copyProperties(orderDTO, order);
        order.setOrderCode(orderCode);
        order.setOrderStatus("DRAFT");
        order.setOrderDate(LocalDate.now());

        // 计算订单金额
        BigDecimal orderAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (orderDTO.getDetails() != null && !orderDTO.getDetails().isEmpty()) {
            for (OrderDetailDTO detailDTO : orderDTO.getDetails()) {
                // 计算明细金额
                BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
                detailDTO.setAmount(amount);

                // 计算折扣金额
                if (detailDTO.getDiscountRate() != null && detailDTO.getDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal detailDiscountAmount = amount.multiply(detailDTO.getDiscountRate())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    detailDTO.setDiscountAmount(detailDiscountAmount);
                    discountAmount = discountAmount.add(detailDiscountAmount);
                }

                // 计算实付金额
                BigDecimal detailDiscountAmt = detailDTO.getDiscountAmount() != null ? detailDTO.getDiscountAmount() : BigDecimal.ZERO;
                BigDecimal payableAmount = amount.subtract(detailDiscountAmt);
                detailDTO.setPayableAmount(payableAmount);
                detailDTO.setShippedQuantity(BigDecimal.ZERO);

                orderAmount = orderAmount.add(amount);
            }
        }

        order.setOrderAmount(orderAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayableAmount(orderAmount.subtract(discountAmount));

        save(order);

        // 保存订单明细
        if (orderDTO.getDetails() != null && !orderDTO.getDetails().isEmpty()) {
            List<SalesOrderDetail> details = orderDTO.getDetails().stream()
                    .map(detailDTO -> {
                        SalesOrderDetail detail = new SalesOrderDetail();
                        BeanUtils.copyProperties(detailDTO, detail);
                        detail.setOrderId(order.getId());
                        return detail;
                    })
                    .collect(Collectors.toList());

            details.forEach(detail -> orderDetailMapper.insert(detail));
        }

        return Result.success(order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveOrder(Long orderId, Long approverId) {
        SalesOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!"DRAFT".equals(order.getOrderStatus())) {
            throw new BusinessException("只有草稿状态的订单才能审核");
        }

        // TODO: 检查客户信用额度
        // TODO: 检查库存

        order.setOrderStatus("APPROVED");
        order.setUpdateBy(approverId);
        updateById(order);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelOrder(Long orderId, String reason) {
        SalesOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if ("SHIPPED".equals(order.getOrderStatus()) || "COMPLETED".equals(order.getOrderStatus())) {
            throw new BusinessException("已发货或已完成的订单不能取消");
        }

        order.setOrderStatus("CANCELLED");
        order.setRemark(reason);
        updateById(order);

        // TODO: 恢复库存(如果已占用)

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> shipOrder(Long orderId) {
        SalesOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!"APPROVED".equals(order.getOrderStatus())) {
            throw new BusinessException("只有审核通过的订单才能发货");
        }

        // TODO: 检查库存是否充足
        // TODO: 生成发货单
        // TODO: 扣减库存

        order.setOrderStatus("SHIPPED");
        updateById(order);

        return Result.success();
    }

    @Override
    public Result<OrderDTO> getOrder(Long orderId) {
        SalesOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);

        // 查询明细
        LambdaQueryWrapper<SalesOrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(SalesOrderDetail::getOrderId, orderId)
                .eq(SalesOrderDetail::getDeleted, 0);
        List<SalesOrderDetail> details = orderDetailMapper.selectList(detailWrapper);

        List<OrderDetailDTO> detailDTOs = details.stream()
                .map(detail -> {
                    OrderDetailDTO dto = new OrderDetailDTO();
                    BeanUtils.copyProperties(detail, dto);
                    return dto;
                })
                .collect(Collectors.toList());

        orderDTO.setDetails(detailDTOs);

        return Result.success(orderDTO);
    }

    @Override
    public Result<PageResult<OrderDTO>> queryOrders(OrderQueryDTO queryDTO) {
        Page<SalesOrder> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SalesOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getCustomerId() != null, SalesOrder::getCustomerId, queryDTO.getCustomerId())
                .like(queryDTO.getCustomerName() != null, SalesOrder::getCustomerName, queryDTO.getCustomerName())
                .eq(queryDTO.getOrderStatus() != null, SalesOrder::getOrderStatus, queryDTO.getOrderStatus())
                .eq(queryDTO.getOrderType() != null, SalesOrder::getOrderType, queryDTO.getOrderType())
                .ge(queryDTO.getStartDate() != null, SalesOrder::getOrderDate, queryDTO.getStartDate())
                .le(queryDTO.getEndDate() != null, SalesOrder::getOrderDate, queryDTO.getEndDate())
                .orderByDesc(SalesOrder::getCreateTime);

        IPage<SalesOrder> orderPage = page(page, queryWrapper);

        List<OrderDTO> orderDTOList = orderPage.getRecords().stream()
                .map(order -> {
                    OrderDTO dto = new OrderDTO();
                    BeanUtils.copyProperties(order, dto);
                    return dto;
                })
                .collect(Collectors.toList());

        PageResult<OrderDTO> pageResult = new PageResult<>();
        pageResult.setRecords(orderDTOList);
        pageResult.setTotal(orderPage.getTotal());
        pageResult.setCurrent(orderPage.getCurrent());
        pageResult.setSize(orderPage.getSize());

        return Result.success(pageResult);
    }

    /**
     * 生成订单编号
     */
    private String generateOrderCode() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomNum = String.format("%04d", RANDOM.nextInt(10000));
        return "SO" + dateStr + randomNum;
    }
}
