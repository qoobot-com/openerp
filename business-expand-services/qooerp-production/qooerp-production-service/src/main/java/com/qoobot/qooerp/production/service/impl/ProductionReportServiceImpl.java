package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.dto.ProductionReportDTO;
import com.qoobot.qooerp.production.entity.ProductionOrder;
import com.qoobot.qooerp.production.entity.ProductionReport;
import com.qoobot.qooerp.production.mapper.ProductionReportMapper;
import com.qoobot.qooerp.production.service.ProductionOrderService;
import com.qoobot.qooerp.production.service.ProductionReportService;
import com.qoobot.qooerp.production.vo.ProductionReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 生产报工Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionReportServiceImpl extends ServiceImpl<ProductionReportMapper, ProductionReport> implements ProductionReportService {

    private final ProductionReportMapper productionReportMapper;
    private final ProductionOrderService productionOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionReport(ProductionReportDTO dto) {
        // 检查报工单号是否已存在
        LambdaQueryWrapper<ProductionReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionReport::getReportNo, dto.getReportNo())
                .eq(ProductionReport::getTenantId, TenantContextHolder.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("报工单号已存在");
        }

        // 验证生产订单是否存在
        ProductionOrder order = productionOrderService.getById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }

        ProductionReport report = BeanUtil.copyProperties(dto, ProductionReport.class);
        report.setStatus("draft");
        report.setTenantId(TenantContextHolder.getTenantId());

        this.save(report);
        log.info("创建生产报工成功: reportNo={}", report.getReportNo());
        return report.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionReport(Long id, ProductionReportDTO dto) {
        ProductionReport report = this.getById(id);
        if (report == null) {
            throw new BusinessException("生产报工不存在");
        }

        // 草稿状态才能修改
        if (!"draft".equals(report.getStatus())) {
            throw new BusinessException("只有草稿状态的报工才能修改");
        }

        // 检查报工单号是否已存在
        if (!report.getReportNo().equals(dto.getReportNo())) {
            LambdaQueryWrapper<ProductionReport> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductionReport::getReportNo, dto.getReportNo())
                    .eq(ProductionReport::getTenantId, TenantContextHolder.getTenantId());
            if (this.count(wrapper) > 0) {
                throw new BusinessException("报工单号已存在");
            }
        }

        BeanUtil.copyProperties(dto, report);
        this.updateById(report);
        log.info("更新生产报工成功: reportNo={}", report.getReportNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionReport(Long id) {
        ProductionReport report = this.getById(id);
        if (report == null) {
            throw new BusinessException("生产报工不存在");
        }

        // 草稿状态才能删除
        if (!"draft".equals(report.getStatus())) {
            throw new BusinessException("只有草稿状态的报工才能删除");
        }

        this.removeById(id);
        log.info("删除生产报工成功: reportNo={}", report.getReportNo());
        return true;
    }

    @Override
    public ProductionReportVO getProductionReportById(Long id) {
        ProductionReport report = this.getById(id);
        if (report == null) {
            throw new BusinessException("生产报工不存在");
        }

        ProductionReportVO vo = BeanUtil.copyProperties(report, ProductionReportVO.class);
        vo.setStatusDesc(getStatusDesc(report.getStatus()));

        // 计算合格率
        if (report.getReportQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = report.getQualifiedQuantity()
                    .divide(report.getReportQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            vo.setQualifiedRate(rate);
        } else {
            vo.setQualifiedRate(BigDecimal.ZERO);
        }

        return vo;
    }

    @Override
    public IPage<ProductionReportVO> queryProductionReportPage(Integer current, Integer size, String reportNo, String status, Long workerId) {
        Page<ProductionReport> page = new Page<>(current, size);
        IPage<ProductionReport> reportPage = productionReportMapper.selectPageByCondition(
                page, reportNo, status, workerId, TenantContextHolder.getTenantId()
        );

        // 转换为VO
        return reportPage.convert(report -> {
            ProductionReportVO vo = BeanUtil.copyProperties(report, ProductionReportVO.class);
            vo.setStatusDesc(getStatusDesc(report.getStatus()));

            // 计算合格率
            if (report.getReportQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = report.getQualifiedQuantity()
                        .divide(report.getReportQuantity(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                vo.setQualifiedRate(rate);
            } else {
                vo.setQualifiedRate(BigDecimal.ZERO);
            }

            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteProductionReport(List<Long> ids) {
        for (Long id : ids) {
            deleteProductionReport(id);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approveProductionReport(Long id) {
        ProductionReport report = this.getById(id);
        if (report == null) {
            throw new BusinessException("生产报工不存在");
        }

        if (!"draft".equals(report.getStatus())) {
            throw new BusinessException("只有草稿状态的报工才能批准");
        }

        report.setStatus("completed");
        this.updateById(report);
        log.info("批准生产报工成功: reportNo={}", report.getReportNo());

        // TODO: 更新生产订单进度
        // TODO: 触发质检流程

        return true;
    }

    @Override
    public ReportStatistics getReportStatistics(Long orderId) {
        LambdaQueryWrapper<ProductionReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionReport::getOrderId, orderId)
                .eq(ProductionReport::getStatus, "completed")
                .eq(ProductionReport::getTenantId, TenantContextHolder.getTenantId());

        List<ProductionReport> reports = this.list(wrapper);

        Long reportCount = (long) reports.size();
        BigDecimal totalReportQuantity = reports.stream()
                .map(ProductionReport::getReportQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalQualifiedQuantity = reports.stream()
                .map(ProductionReport::getQualifiedQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRejectQuantity = reports.stream()
                .map(ProductionReport::getRejectQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalWorkTime = reports.stream()
                .map(ProductionReport::getWorkTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReportStatistics(reportCount, totalReportQuantity, totalQualifiedQuantity, totalRejectQuantity, totalWorkTime);
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(String status) {
        return switch (status) {
            case "draft" -> "草稿";
            case "approved" -> "已批准";
            case "completed" -> "已完成";
            default -> status;
        };
    }
}
