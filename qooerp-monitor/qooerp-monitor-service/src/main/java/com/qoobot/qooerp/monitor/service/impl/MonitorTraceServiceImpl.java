package com.qoobot.qooerp.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.monitor.dto.MonitorTraceDTO;
import com.qoobot.qooerp.monitor.dto.TraceQueryDTO;
import com.qoobot.qooerp.monitor.entity.MonitorTrace;
import com.qoobot.qooerp.monitor.mapper.MonitorTraceMapper;
import com.qoobot.qooerp.monitor.service.MonitorTraceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorTraceServiceImpl implements MonitorTraceService {

    private final MonitorTraceMapper traceMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<MonitorTraceDTO> getTrace(String traceId) {
        LambdaQueryWrapper<MonitorTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorTrace::getTraceId, traceId);
        wrapper.eq(MonitorTrace::getTenantId, TenantContextHolder.getTenantId());
        wrapper.orderByAsc(MonitorTrace::getStartTime);

        List<MonitorTrace> traces = traceMapper.selectList(wrapper);
        return traces.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Page<MonitorTraceDTO> listTraces(TraceQueryDTO dto) {
        LambdaQueryWrapper<MonitorTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorTrace::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getTraceId() != null) {
            wrapper.like(MonitorTrace::getTraceId, dto.getTraceId());
        }
        if (dto.getServiceName() != null) {
            wrapper.eq(MonitorTrace::getServiceName, dto.getServiceName());
        }
        if (dto.getOperationName() != null) {
            wrapper.like(MonitorTrace::getSpanName, dto.getOperationName());
        }
        if (dto.getStatusCode() != null) {
            wrapper.eq(MonitorTrace::getStatusCode, dto.getStatusCode());
        }
        if (dto.getStartTimeStart() != null) {
            wrapper.ge(MonitorTrace::getStartTime, dto.getStartTimeStart());
        }
        if (dto.getStartTimeEnd() != null) {
            wrapper.le(MonitorTrace::getStartTime, dto.getStartTimeEnd());
        }
        if (dto.getMinDuration() != null) {
            wrapper.ge(MonitorTrace::getDurationMs, dto.getMinDuration());
        }
        if (dto.getMaxDuration() != null) {
            wrapper.le(MonitorTrace::getDurationMs, dto.getMaxDuration());
        }

        wrapper.orderByDesc(MonitorTrace::getStartTime);

        Page<MonitorTrace> page = new Page<>(dto.getCurrent(), dto.getSize());
        page = traceMapper.selectPage(page, wrapper);
        Page<MonitorTraceDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<MonitorTraceDTO> getSpans(String traceId) {
        LambdaQueryWrapper<MonitorTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorTrace::getTraceId, traceId);
        wrapper.eq(MonitorTrace::getTenantId, TenantContextHolder.getTenantId());
        wrapper.orderByAsc(MonitorTrace::getStartTime);

        List<MonitorTrace> traces = traceMapper.selectList(wrapper);
        return traces.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private MonitorTraceDTO convertToDTO(MonitorTrace entity) {
        MonitorTraceDTO dto = new MonitorTraceDTO();
        dto.setId(entity.getId());
        dto.setTraceId(entity.getTraceId());
        dto.setSpanId(entity.getSpanId());
        dto.setParentSpanId(entity.getParentSpanId());
        dto.setServiceName(entity.getServiceName());
        dto.setOperationName(entity.getSpanName());
        dto.setStartTime(entity.getStartTime() != null ? entity.getStartTime().toLocalDateTime() : null);
        dto.setDuration(entity.getDurationMs());
        dto.setStatusCode(entity.getStatusCode() != null ? Integer.valueOf(entity.getStatusCode()) : null);
        dto.setCreateTime(entity.getCreatedTime() != null ? entity.getCreatedTime().toLocalDateTime() : null);

        try {
            if (entity.getTags() != null) {
                List<MonitorTraceDTO.Tag> tags = objectMapper.readValue(entity.getTags(),
                        new TypeReference<List<MonitorTraceDTO.Tag>>() {});
                dto.setTags(tags);
            }
            if (entity.getLogs() != null) {
                List<MonitorTraceDTO.LogEntry> logs = objectMapper.readValue(entity.getLogs(),
                        new TypeReference<List<MonitorTraceDTO.LogEntry>>() {});
                dto.setLogs(logs);
            }
        } catch (Exception e) {
            // ignore parse error
        }

        return dto;
    }
}
