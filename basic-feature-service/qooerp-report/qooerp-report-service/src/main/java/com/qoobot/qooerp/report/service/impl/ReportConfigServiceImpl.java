package com.qoobot.qooerp.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.report.dto.ReportConfigCreateDTO;
import com.qoobot.qooerp.report.dto.ReportConfigDTO;
import com.qoobot.qooerp.report.dto.ReportConfigQueryDTO;
import com.qoobot.qooerp.report.dto.ReportConfigUpdateDTO;
import com.qoobot.qooerp.report.entity.ReportConfig;
import com.qoobot.qooerp.report.mapper.ReportConfigMapper;
import com.qoobot.qooerp.report.service.ReportConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 报表配置服务实现
 *
 * @author Auto
 * @since 2026-02-18
 */
@Slf4j
@Service
public class ReportConfigServiceImpl extends ServiceImpl<ReportConfigMapper, ReportConfig>
        implements ReportConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportConfigDTO createConfig(ReportConfigCreateDTO dto) {
        log.info("创建配置: {}", dto);

        ReportConfig config = new ReportConfig();
        BeanUtils.copyProperties(dto, config);

        // 设置租户ID
        config.setTenantId(getCurrentTenantId());

        save(config);

        log.info("配置创建成功: id={}", config.getId());

        return convertToDTO(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(ReportConfigUpdateDTO dto) {
        log.info("更新配置: {}", dto);

        ReportConfig config = getById(dto.getId());
        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        BeanUtils.copyProperties(dto, config, "id", "reportId", "tenantId");

        return updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConfig(Long id) {
        log.info("删除配置: id={}", id);

        ReportConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        return removeById(id);
    }

    @Override
    public ReportConfigDTO getConfigById(Long id) {
        ReportConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        return convertToDTO(config);
    }

    @Override
    public PageResult<ReportConfigDTO> queryConfigs(ReportConfigQueryDTO queryDTO) {
        log.info("分页查询配置: {}", queryDTO);

        LambdaQueryWrapper<ReportConfig> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getReportId() != null) {
            wrapper.eq(ReportConfig::getReportId, queryDTO.getReportId());
        }

        if (StringUtils.hasText(queryDTO.getConfigName())) {
            wrapper.like(ReportConfig::getConfigName, queryDTO.getConfigName());
        }

        if (StringUtils.hasText(queryDTO.getChartType())) {
            wrapper.eq(ReportConfig::getChartType, queryDTO.getChartType());
        }

        wrapper.orderByDesc(ReportConfig::getCreateTime);

        Page<ReportConfig> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        IPage<ReportConfig> result = page(page, wrapper);

        List<ReportConfigDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), dtoList);
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
    private ReportConfigDTO convertToDTO(ReportConfig config) {
        ReportConfigDTO dto = new ReportConfigDTO();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }
}
