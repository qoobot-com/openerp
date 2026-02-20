package com.qoobot.qooerp.sales.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.sales.order.domain.SalesOrder;
import com.qoobot.qooerp.sales.order.mapper.SalesOrderMapper;
import com.qoobot.qooerp.sales.report.dto.*;
import com.qoobot.qooerp.sales.report.service.SalesReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 销售报表 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesReportServiceImpl implements SalesReportService {

    private final SalesOrderMapper orderMapper;

    @Override
    public SalesStatisticsDTO getSalesStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("获取销售统计, startDate: {}, endDate: {}", startDate, endDate);

        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(SalesOrder::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(SalesOrder::getCreateTime, endDate.atTime(23, 59, 59));
        }

        List<SalesOrder> orders = orderMapper.selectList(wrapper);

        SalesStatisticsDTO dto = new SalesStatisticsDTO();

        long totalOrders = orders.size();
        long cancelledOrders = orders.stream().filter(o -> "CANCELLED".equals(o.getOrderStatus())).count();
        long completedOrders = orders.stream().filter(o -> "COMPLETED".equals(o.getOrderStatus())).count();

        BigDecimal totalAmount = orders.stream()
                .map(SalesOrder::getOrderAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidAmount = orders.stream()
                .map(SalesOrder::getPayableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal refundAmount = BigDecimal.ZERO; // TODO: 从退货表统计

        BigDecimal avgOrderAmount = totalOrders > 0 ? totalAmount.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        BigDecimal completionRate = totalOrders > 0 ?
                BigDecimal.valueOf(completedOrders * 100).divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        BigDecimal paymentRate = totalAmount.compareTo(BigDecimal.ZERO) > 0 ?
                paidAmount.multiply(BigDecimal.valueOf(100)).divide(totalAmount, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        dto.setTotalOrders(totalOrders);
        dto.setCancelledOrders(cancelledOrders);
        dto.setCompletedOrders(completedOrders);
        dto.setTotalAmount(totalAmount);
        dto.setPaidAmount(paidAmount);
        dto.setUnpaidAmount(totalAmount.subtract(paidAmount));
        dto.setRefundAmount(refundAmount);
        dto.setAvgOrderAmount(avgOrderAmount);
        dto.setCompletionRate(completionRate);
        dto.setPaymentRate(paymentRate);

        return dto;
    }

    @Override
    public List<SalesTrendDTO> getSalesTrend(LocalDate startDate, LocalDate endDate) {
        log.info("获取销售趋势, startDate: {}, endDate: {}", startDate, endDate);

        List<SalesTrendDTO> trendList = new ArrayList<>();

        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(SalesOrder::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(SalesOrder::getCreateTime, endDate.atTime(23, 59, 59));
        }

        List<SalesOrder> orders = orderMapper.selectList(wrapper);

        // TODO: 按日期分组统计

        return trendList;
    }

    @Override
    public List<SalesRankingDTO> getProductRanking(LocalDate startDate, LocalDate endDate, Integer topN) {
        log.info("获取商品销售排行, startDate: {}, endDate: {}, topN: {}", startDate, endDate, topN);

        // TODO: 统计商品销售排行
        return new ArrayList<>();
    }

    @Override
    public List<SalesRankingDTO> getCustomerRanking(LocalDate startDate, LocalDate endDate, Integer topN) {
        log.info("获取客户销售排行, startDate: {}, endDate: {}, topN: {}", startDate, endDate, topN);

        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(SalesOrder::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(SalesOrder::getCreateTime, endDate.atTime(23, 59, 59));
        }

        List<SalesOrder> orders = orderMapper.selectList(wrapper);

        // TODO: 按客户分组统计
        return new ArrayList<>();
    }

    @Override
    public List<SalesRankingDTO> getSalespersonRanking(LocalDate startDate, LocalDate endDate, Integer topN) {
        log.info("获取销售员业绩排行, startDate: {}, endDate: {}, topN: {}", startDate, endDate, topN);

        // TODO: 统计销售员业绩排行
        return new ArrayList<>();
    }
}
