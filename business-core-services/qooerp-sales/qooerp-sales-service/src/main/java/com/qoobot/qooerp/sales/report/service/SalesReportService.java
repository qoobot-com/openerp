package com.qoobot.qooerp.sales.report.service;

import com.qoobot.qooerp.sales.report.dto.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 销售报表 Service 接口
 */
public interface SalesReportService {

    /**
     * 获取销售统计
     */
    SalesStatisticsDTO getSalesStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取销售趋势
     */
    List<SalesTrendDTO> getSalesTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 获取商品销售排行
     */
    List<SalesRankingDTO> getProductRanking(LocalDate startDate, LocalDate endDate, Integer topN);

    /**
     * 获取客户销售排行
     */
    List<SalesRankingDTO> getCustomerRanking(LocalDate startDate, LocalDate endDate, Integer topN);

    /**
     * 获取销售员业绩排行
     */
    List<SalesRankingDTO> getSalespersonRanking(LocalDate startDate, LocalDate endDate, Integer topN);
}
