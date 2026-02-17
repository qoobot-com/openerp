package com.qoobot.qooerp.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.entity.MonitorAlert;
import com.qoobot.qooerp.monitor.entity.MonitorAlertHandle;
import com.qoobot.qooerp.monitor.mapper.MonitorAlertHandleMapper;
import com.qoobot.qooerp.monitor.mapper.MonitorAlertMapper;
import com.qoobot.qooerp.monitor.service.MonitorAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorAlertServiceImpl implements MonitorAlertService {

    private final MonitorAlertMapper alertMapper;
    private final MonitorAlertHandleMapper alertHandleMapper;

    @Override
    public Page<MonitorAlertDTO> listAlerts(AlertQueryDTO dto) {
        LambdaQueryWrapper<MonitorAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorAlert::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getServiceName() != null) {
            wrapper.like(MonitorAlert::getServiceName, dto.getServiceName());
        }
        if (dto.getAlertType() != null) {
            wrapper.eq(MonitorAlert::getAlertType, dto.getAlertType());
        }
        if (dto.getAlertLevel() != null) {
            wrapper.eq(MonitorAlert::getAlertLevel, dto.getAlertLevel());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(MonitorAlert::getStatus, dto.getStatus());
        }
        if (dto.getHandlerId() != null) {
            wrapper.eq(MonitorAlert::getHandlerId, dto.getHandlerId());
        }
        if (dto.getAlertTimeStart() != null) {
            wrapper.ge(MonitorAlert::getAlertTime, dto.getAlertTimeStart());
        }
        if (dto.getAlertTimeEnd() != null) {
            wrapper.le(MonitorAlert::getAlertTime, dto.getAlertTimeEnd());
        }

        wrapper.orderByDesc(MonitorAlert::getAlertTime);

        Page<MonitorAlert> page = alertMapper.selectPage(dto, wrapper);
        Page<MonitorAlertDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return result;
    }

    @Override
    public MonitorAlertDTO getAlert(Long id) {
        MonitorAlert entity = alertMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("告警不存在");
        }
        return convertToDTO(entity);
    }

    @Override
    public void handleAlert(Long id, AlertHandleDTO dto) {
        MonitorAlert entity = alertMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("告警不存在");
        }

        entity.setStatus(1);
        entity.setHandlerId(dto.getHandlerId());
        entity.setHandleTime(LocalDateTime.now());
        entity.setHandleRemark(dto.getHandleRemark());
        entity.setUpdateTime(LocalDateTime.now());
        alertMapper.updateById(entity);

        MonitorAlertHandle handle = new MonitorAlertHandle();
        handle.setAlertId(id);
        handle.setHandlerId(dto.getHandlerId());
        handle.setHandleAction(dto.getHandleAction());
        handle.setHandleRemark(dto.getHandleRemark());
        handle.setHandleTime(LocalDateTime.now());
        handle.setTenantId(TenantContextHolder.getTenantId());
        alertHandleMapper.insert(handle);
    }

    @Override
    public AlertStatisticsDTO getStatistics(AlertStatisticsQueryDTO dto) {
        LambdaQueryWrapper<MonitorAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorAlert::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getServiceName() != null) {
            wrapper.like(MonitorAlert::getServiceName, dto.getServiceName());
        }
        if (dto.getAlertType() != null) {
            wrapper.eq(MonitorAlert::getAlertType, dto.getAlertType());
        }
        if (dto.getAlertLevel() != null) {
            wrapper.eq(MonitorAlert::getAlertLevel, dto.getAlertLevel());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(MonitorAlert::getAlertTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(MonitorAlert::getAlertTime, dto.getEndTime());
        }

        List<MonitorAlert> alerts = alertMapper.selectList(wrapper);

        AlertStatisticsDTO statistics = new AlertStatisticsDTO();
        statistics.setTotalCount((long) alerts.size());
        statistics.setPendingCount(alerts.stream().filter(a -> a.getStatus() == 0).count());
        statistics.setHandledCount(alerts.stream().filter(a -> a.getStatus() == 1).count());
        statistics.setClosedCount(alerts.stream().filter(a -> a.getStatus() == 2).count());

        Map<Integer, Long> countByLevel = alerts.stream()
                .collect(Collectors.groupingBy(MonitorAlert::getAlertLevel, Collectors.counting()));
        statistics.setCountByLevel(countByLevel);

        Map<String, Long> countByType = alerts.stream()
                .collect(Collectors.groupingBy(MonitorAlert::getAlertType, Collectors.counting()));
        statistics.setCountByType(countByType);

        Map<String, Long> countByService = alerts.stream()
                .collect(Collectors.groupingBy(MonitorAlert::getServiceName, Collectors.counting()));
        statistics.setCountByService(countByService);

        return statistics;
    }

    private MonitorAlertDTO convertToDTO(MonitorAlert entity) {
        MonitorAlertDTO dto = new MonitorAlertDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
