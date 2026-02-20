package com.qoobot.qooerp.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.report.dto.DashboardCreateDTO;
import com.qoobot.qooerp.report.dto.DashboardDTO;
import com.qoobot.qooerp.report.dto.DashboardQueryDTO;
import com.qoobot.qooerp.report.dto.DashboardUpdateDTO;
import com.qoobot.qooerp.report.entity.Dashboard;
import com.qoobot.qooerp.report.enums.DashboardStatus;
import com.qoobot.qooerp.report.mapper.DashboardMapper;
import com.qoobot.qooerp.report.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DashboardServiceImpl extends ServiceImpl<DashboardMapper, Dashboard> implements DashboardService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DashboardDTO createDashboard(DashboardCreateDTO dto) {
        Dashboard dashboard = new Dashboard();
        BeanUtils.copyProperties(dto, dashboard);
        dashboard.setDashboardNo(generateDashboardNo());
        dashboard.setStatus(DashboardStatus.ENABLED.getCode());
        dashboard.setTenantId(1L);
        save(dashboard);
        return convertToDTO(dashboard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDashboard(DashboardUpdateDTO dto) {
        Dashboard dashboard = getById(dto.getId());
        if (dashboard == null) {
            throw new BusinessException("仪表盘不存在");
        }
        BeanUtils.copyProperties(dto, dashboard, "id", "dashboardNo", "tenantId");
        return updateById(dashboard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDashboard(Long id) {
        Dashboard dashboard = getById(id);
        if (dashboard == null) {
            throw new BusinessException("仪表盘不存在");
        }
        return removeById(id);
    }

    @Override
    public DashboardDTO getDashboardById(Long id) {
        Dashboard dashboard = getById(id);
        if (dashboard == null) {
            throw new BusinessException("仪表盘不存在");
        }
        return convertToDTO(dashboard);
    }

    @Override
    public PageResult<DashboardDTO> queryDashboards(DashboardQueryDTO queryDTO) {
        LambdaQueryWrapper<Dashboard> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getDashboardName())) {
            wrapper.like(Dashboard::getDashboardName, queryDTO.getDashboardName());
        }
        if (queryDTO.getDashboardType() != null) {
            wrapper.eq(Dashboard::getDashboardType, queryDTO.getDashboardType());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Dashboard::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByDesc(Dashboard::getCreateTime);

        Page<Dashboard> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        IPage<Dashboard> result = page(page, wrapper);

        List<DashboardDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), dtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishDashboard(Long id) {
        Dashboard dashboard = getById(id);
        if (dashboard == null) {
            throw new BusinessException("仪表盘不存在");
        }
        dashboard.setStatus(DashboardStatus.ENABLED.getCode());
        return updateById(dashboard);
    }

    private String generateDashboardNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        Long count = lambdaQuery()
                .likeRight(Dashboard::getDashboardNo, "DASH" + dateStr)
                .count();
        return String.format("DASH%s%04d", dateStr, count + 1);
    }

    private DashboardDTO convertToDTO(Dashboard dashboard) {
        DashboardDTO dto = new DashboardDTO();
        BeanUtils.copyProperties(dashboard, dto);
        return dto;
    }
}
