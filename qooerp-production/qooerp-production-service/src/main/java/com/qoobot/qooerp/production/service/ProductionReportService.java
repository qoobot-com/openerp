package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.dto.ProductionReportDTO;
import com.qoobot.qooerp.production.entity.ProductionReport;
import com.qoobot.qooerp.production.vo.ProductionReportVO;

import java.util.List;

/**
 * 生产报工Service接口
 */
public interface ProductionReportService extends IService<ProductionReport> {

    /**
     * 创建生产报工
     *
     * @param dto 生产报工DTO
     * @return 生产报工ID
     */
    Long createProductionReport(ProductionReportDTO dto);

    /**
     * 更新生产报工
     *
     * @param id 生产报工ID
     * @param dto 生产报工DTO
     * @return 是否成功
     */
    Boolean updateProductionReport(Long id, ProductionReportDTO dto);

    /**
     * 删除生产报工
     *
     * @param id 生产报工ID
     * @return 是否成功
     */
    Boolean deleteProductionReport(Long id);

    /**
     * 根据ID查询生产报工
     *
     * @param id 生产报工ID
     * @return 生产报工VO
     */
    ProductionReportVO getProductionReportById(Long id);

    /**
     * 分页查询生产报工
     *
     * @param current 当前页
     * @param size 每页大小
     * @param reportNo 报工单号
     * @param status 状态
     * @param workerId 工人ID
     * @return 分页结果
     */
    IPage<ProductionReportVO> queryProductionReportPage(Integer current, Integer size, String reportNo, String status, Long workerId);

    /**
     * 批量删除生产报工
     *
     * @param ids 生产报工ID列表
     * @return 是否成功
     */
    Boolean batchDeleteProductionReport(List<Long> ids);

    /**
     * 批准生产报工
     *
     * @param id 生产报工ID
     * @return 是否成功
     */
    Boolean approveProductionReport(Long id);

    /**
     * 统计报工数据
     *
     * @param orderId 生产订单ID
     * @return 统计结果
     */
    ReportStatistics getReportStatistics(Long orderId);

    /**
     * 报工统计VO
     */
    class ReportStatistics {
        private Long reportCount;
        private java.math.BigDecimal totalReportQuantity;
        private java.math.BigDecimal totalQualifiedQuantity;
        private java.math.BigDecimal totalRejectQuantity;
        private java.math.BigDecimal qualifiedRate;
        private java.math.BigDecimal totalWorkTime;

        public ReportStatistics(Long reportCount, java.math.BigDecimal totalReportQuantity,
                               java.math.BigDecimal totalQualifiedQuantity, java.math.BigDecimal totalRejectQuantity,
                               java.math.BigDecimal totalWorkTime) {
            this.reportCount = reportCount;
            this.totalReportQuantity = totalReportQuantity;
            this.totalQualifiedQuantity = totalQualifiedQuantity;
            this.totalRejectQuantity = totalRejectQuantity;
            this.totalWorkTime = totalWorkTime;
            if (totalReportQuantity != null && totalReportQuantity.compareTo(java.math.BigDecimal.ZERO) > 0) {
                this.qualifiedRate = totalQualifiedQuantity
                        .divide(totalReportQuantity, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(java.math.BigDecimal.valueOf(100))
                        .setScale(2, java.math.RoundingMode.HALF_UP);
            } else {
                this.qualifiedRate = java.math.BigDecimal.ZERO;
            }
        }

        public Long getReportCount() { return reportCount; }
        public java.math.BigDecimal getTotalReportQuantity() { return totalReportQuantity; }
        public java.math.BigDecimal getTotalQualifiedQuantity() { return totalQualifiedQuantity; }
        public java.math.BigDecimal getTotalRejectQuantity() { return totalRejectQuantity; }
        public java.math.BigDecimal getQualifiedRate() { return qualifiedRate; }
        public java.math.BigDecimal getTotalWorkTime() { return totalWorkTime; }
    }
}
