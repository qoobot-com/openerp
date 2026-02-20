package com.qoobot.qooerp.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.report.dto.ReportCreateDTO;
import com.qoobot.qooerp.report.dto.ReportDTO;
import com.qoobot.qooerp.report.dto.ReportQueryDTO;
import com.qoobot.qooerp.report.dto.ReportUpdateDTO;
import com.qoobot.qooerp.report.entity.Report;
import com.qoobot.qooerp.report.enums.ReportStatus;
import com.qoobot.qooerp.report.enums.ReportType;
import com.qoobot.qooerp.report.mapper.ReportMapper;
import com.qoobot.qooerp.report.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报表服务实现
 *
 * @author Auto
 * @since 2026-02-18
 */
@Slf4j
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportDTO createReport(ReportCreateDTO dto) {
        log.info("创建报表: {}", dto);

        Report report = new Report();
        BeanUtils.copyProperties(dto, report);

        // 生成报表编号
        report.setReportNo(generateReportNo());

        // 设置状态为草稿
        report.setStatus(ReportStatus.DRAFT.getCode());

        // 设置租户ID（从上下文获取）
        report.setTenantId(getCurrentTenantId());

        save(report);

        log.info("报表创建成功: id={}, reportNo={}", report.getId(), report.getReportNo());

        return convertToDTO(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReport(ReportUpdateDTO dto) {
        log.info("更新报表: {}", dto);

        Report report = getById(dto.getId());
        if (report == null) {
            throw new BusinessException("报表不存在");
        }

        // 只有草稿状态的报表可以编辑
        if (!ReportStatus.DRAFT.getCode().equals(report.getStatus())) {
            throw new BusinessException("只有草稿状态的报表可以编辑");
        }

        BeanUtils.copyProperties(dto, report, "id", "reportNo", "tenantId");

        return updateById(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReport(Long id) {
        log.info("删除报表: id={}", id);

        Report report = getById(id);
        if (report == null) {
            throw new BusinessException("报表不存在");
        }

        // 只有草稿状态的报表可以删除
        if (!ReportStatus.DRAFT.getCode().equals(report.getStatus())) {
            throw new BusinessException("只有草稿状态的报表可以删除");
        }

        return removeById(id);
    }

    @Override
    public ReportDTO getReportById(Long id) {
        Report report = getById(id);
        if (report == null) {
            throw new BusinessException("报表不存在");
        }
        return convertToDTO(report);
    }

    @Override
    public PageResult<ReportDTO> queryReports(ReportQueryDTO queryDTO) {
        log.info("分页查询报表: {}", queryDTO);

        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getReportName())) {
            wrapper.like(Report::getReportName, queryDTO.getReportName());
        }

        if (queryDTO.getReportType() != null) {
            wrapper.eq(Report::getReportType, queryDTO.getReportType());
        }

        if (StringUtils.hasText(queryDTO.getCategory())) {
            wrapper.eq(Report::getCategory, queryDTO.getCategory());
        }

        if (queryDTO.getStatus() != null) {
            wrapper.eq(Report::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByDesc(Report::getCreateTime);

        Page<Report> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        IPage<Report> result = page(page, wrapper);

        List<ReportDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), dtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishReport(Long id) {
        log.info("发布报表: id={}", id);

        Report report = getById(id);
        if (report == null) {
            throw new BusinessException("报表不存在");
        }

        if (ReportStatus.PUBLISHED.getCode().equals(report.getStatus())) {
            throw new BusinessException("报表已发布");
        }

        report.setStatus(ReportStatus.PUBLISHED.getCode());

        return updateById(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean archiveReport(Long id) {
        log.info("归档报表: id={}", id);

        Report report = getById(id);
        if (report == null) {
            throw new BusinessException("报表不存在");
        }

        if (!ReportStatus.PUBLISHED.getCode().equals(report.getStatus())) {
            throw new BusinessException("只有已发布的报表可以归档");
        }

        report.setStatus(ReportStatus.ARCHIVED.getCode());

        return updateById(report);
    }

    /**
     * 生成报表编号
     */
    private String generateReportNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        Long count = lambdaQuery()
                .likeRight(Report::getReportNo, "RPT" + dateStr)
                .count();
        return String.format("RPT%s%04d", dateStr, count + 1);
    }

    /**
     * 获取当前租户ID
     */
    private Long getCurrentTenantId() {
        // TODO: 从上下文获取当前租户ID
        return 1L;
    }

    /**
     * 转换为DTO
     */
    private ReportDTO convertToDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        BeanUtils.copyProperties(report, dto);

        // 添加类型描述
        if (report.getReportType() != null) {
            ReportType type = ReportType.getByCode(report.getReportType());
            if (type != null) {
                dto.setReportTypeDesc(type.getDesc());
            }
        }

        return dto;
    }
}
