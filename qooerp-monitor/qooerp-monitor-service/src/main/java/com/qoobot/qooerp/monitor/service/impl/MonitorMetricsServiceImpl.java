package com.qoobot.qooerp.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.entity.MonitorMetrics;
import com.qoobot.qooerp.monitor.mapper.MonitorMetricsMapper;
import com.qoobot.qooerp.monitor.service.MonitorMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorMetricsServiceImpl implements MonitorMetricsService {

    private final MonitorMetricsMapper metricsMapper;

    @Override
    public MonitorMetricsDTO getCurrentMetrics(String serviceName) {
        MonitorMetricsDTO dto = new MonitorMetricsDTO();
        dto.setServiceName(serviceName);

        List<String> metricTypes = Arrays.asList("cpu", "memory", "disk", "network", "jvm");

        for (String metricType : metricTypes) {
            LambdaQueryWrapper<MonitorMetrics> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MonitorMetrics::getServiceName, serviceName);
            wrapper.eq(MonitorMetrics::getMetricType, metricType);
            wrapper.orderByDesc(MonitorMetrics::getTime);
            wrapper.last("LIMIT 10");

            List<MonitorMetrics> metrics = metricsMapper.selectList(wrapper);
            List<MonitorMetricsDTO.MetricItem> items = metrics.stream().map(m -> {
                MonitorMetricsDTO.MetricItem item = new MonitorMetricsDTO.MetricItem();
                item.setMetricName(m.getMetricName());
                item.setMetricValue(BigDecimal.valueOf(m.getMetricValue()));
                item.setMetricUnit(m.getMetricUnit());
                return item;
            }).collect(Collectors.toList());

            switch (metricType) {
                case "cpu":
                    dto.setCpuMetrics(items);
                    break;
                case "memory":
                    dto.setMemoryMetrics(items);
                    break;
                case "disk":
                    dto.setDiskMetrics(items);
                    break;
                case "network":
                    dto.setNetworkMetrics(items);
                    break;
                case "jvm":
                    dto.setJvmMetrics(items);
                    break;
            }
        }

        return dto;
    }

    @Override
    public Page<MonitorMetricsDTO> getHistoryMetrics(MetricsQueryDTO dto) {
        LambdaQueryWrapper<MonitorMetrics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorMetrics::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getServiceName() != null) {
            wrapper.eq(MonitorMetrics::getServiceName, dto.getServiceName());
        }
        if (dto.getMetricType() != null) {
            wrapper.eq(MonitorMetrics::getMetricType, dto.getMetricType());
        }
        if (dto.getMetricName() != null) {
            wrapper.eq(MonitorMetrics::getMetricName, dto.getMetricName());
        }
        if (dto.getCollectTimeStart() != null) {
            wrapper.ge(MonitorMetrics::getTime, dto.getCollectTimeStart());
        }
        if (dto.getCollectTimeEnd() != null) {
            wrapper.le(MonitorMetrics::getTime, dto.getCollectTimeEnd());
        }

        wrapper.orderByDesc(MonitorMetrics::getTime);

        Page<MonitorMetrics> page = new Page<>(dto.getCurrent(), dto.getSize());
        page = metricsMapper.selectPage(page, wrapper);
        Page<MonitorMetricsDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return result;
    }

    @Override
    public MetricsSummaryDTO getSummary(MetricsSummaryQueryDTO dto) {
        LambdaQueryWrapper<MonitorMetrics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorMetrics::getTenantId, TenantContextHolder.getTenantId());
        wrapper.eq(MonitorMetrics::getServiceName, dto.getServiceName());

        if (dto.getStartTime() != null) {
            wrapper.ge(MonitorMetrics::getTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(MonitorMetrics::getTime, dto.getEndTime());
        }

        List<MonitorMetrics> metrics = metricsMapper.selectList(wrapper);

        MetricsSummaryDTO summary = new MetricsSummaryDTO();
        summary.setServiceName(dto.getServiceName());

        List<MonitorMetrics> cpuMetrics = metrics.stream()
                .filter(m -> "cpu".equals(m.getMetricType()))
                .collect(Collectors.toList());
        if (!cpuMetrics.isEmpty()) {
            BigDecimal avg = cpuMetrics.stream()
                    .map(m -> BigDecimal.valueOf(m.getMetricValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(cpuMetrics.size()), 2, RoundingMode.HALF_UP);
            summary.setCpuUsageAvg(avg);
        }

        List<MonitorMetrics> memoryMetrics = metrics.stream()
                .filter(m -> "memory".equals(m.getMetricType()))
                .collect(Collectors.toList());
        if (!memoryMetrics.isEmpty()) {
            BigDecimal avg = memoryMetrics.stream()
                    .map(m -> BigDecimal.valueOf(m.getMetricValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(memoryMetrics.size()), 2, RoundingMode.HALF_UP);
            summary.setMemoryUsageAvg(avg);
        }

        Map<String, BigDecimal> jvmMetricsAvg = new HashMap<>();
        List<MonitorMetrics> jvmMetrics = metrics.stream()
                .filter(m -> "jvm".equals(m.getMetricType()))
                .collect(Collectors.toList());
        Map<String, List<MonitorMetrics>> jvmByMetricName = jvmMetrics.stream()
                .collect(Collectors.groupingBy(MonitorMetrics::getMetricName));
        jvmByMetricName.forEach((name, list) -> {
            BigDecimal avg = list.stream()
                    .map(m -> BigDecimal.valueOf(m.getMetricValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
            jvmMetricsAvg.put(name, avg);
        });
        summary.setJvmMetricsAvg(jvmMetricsAvg);

        return summary;
    }

    @Override
    public List<MetricsTrendDTO> getTrend(MetricsTrendQueryDTO dto) {
        LambdaQueryWrapper<MonitorMetrics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorMetrics::getTenantId, TenantContextHolder.getTenantId());
        wrapper.eq(MonitorMetrics::getServiceName, dto.getServiceName());

        if (dto.getMetricName() != null) {
            wrapper.eq(MonitorMetrics::getMetricName, dto.getMetricName());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(MonitorMetrics::getTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(MonitorMetrics::getTime, dto.getEndTime());
        }

        wrapper.orderByAsc(MonitorMetrics::getTime);

        List<MonitorMetrics> metrics = metricsMapper.selectList(wrapper);

        MetricsTrendDTO trendDTO = new MetricsTrendDTO();
        trendDTO.setMetricName(dto.getMetricName());

        List<MetricsTrendDTO.TrendData> trendList = metrics.stream().map(m -> {
            MetricsTrendDTO.TrendData data = new MetricsTrendDTO.TrendData();
            data.setTimestamp(m.getTime() != null ? m.getTime().toLocalDateTime() : null);
            data.setValue(BigDecimal.valueOf(m.getMetricValue()));
            return data;
        }).collect(Collectors.toList());

        trendDTO.setTrendList(trendList);

        return Collections.singletonList(trendDTO);
    }

    private MonitorMetricsDTO convertToDTO(MonitorMetrics entity) {
        MonitorMetricsDTO dto = new MonitorMetricsDTO();
        dto.setServiceName(entity.getServiceName());
        MonitorMetricsDTO.MetricItem item = new MonitorMetricsDTO.MetricItem();
        item.setMetricName(entity.getMetricName());
        item.setMetricValue(BigDecimal.valueOf(entity.getMetricValue()));
        item.setMetricUnit(entity.getMetricUnit());
        dto.setCollectTime(entity.getTime() != null ? entity.getTime().toLocalDateTime() : null);
        return dto;
    }
}
