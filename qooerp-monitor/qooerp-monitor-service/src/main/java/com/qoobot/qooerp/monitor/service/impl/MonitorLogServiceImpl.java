package com.qoobot.qooerp.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.monitor.dto.LogQueryDTO;
import com.qoobot.qooerp.monitor.dto.MonitorLogDTO;
import com.qoobot.qooerp.monitor.entity.MonitorLog;
import com.qoobot.qooerp.monitor.mapper.MonitorLogMapper;
import com.qoobot.qooerp.monitor.service.MonitorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorLogServiceImpl implements MonitorLogService {

    private final MonitorLogMapper logMapper;

    @Override
    public Page<MonitorLogDTO> listLogs(LogQueryDTO dto) {
        LambdaQueryWrapper<MonitorLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorLog::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getServiceName() != null) {
            wrapper.like(MonitorLog::getServiceName, dto.getServiceName());
        }
        if (dto.getLogType() != null) {
            wrapper.eq(MonitorLog::getLogType, dto.getLogType());
        }
        if (dto.getLogLevel() != null) {
            wrapper.eq(MonitorLog::getLogLevel, dto.getLogLevel());
        }
        if (dto.getLogTimeStart() != null) {
            wrapper.ge(MonitorLog::getLogTime, dto.getLogTimeStart());
        }
        if (dto.getLogTimeEnd() != null) {
            wrapper.le(MonitorLog::getLogTime, dto.getLogTimeEnd());
        }
        if (dto.getKeyword() != null) {
            wrapper.like(MonitorLog::getLogContent, dto.getKeyword());
        }

        wrapper.orderByDesc(MonitorLog::getLogTime);

        Page<MonitorLog> page = logMapper.selectPage(dto, wrapper);
        Page<MonitorLogDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<String> listLogTypes() {
        return Arrays.asList("monitor", "access", "error");
    }

    private MonitorLogDTO convertToDTO(MonitorLog entity) {
        MonitorLogDTO dto = new MonitorLogDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
