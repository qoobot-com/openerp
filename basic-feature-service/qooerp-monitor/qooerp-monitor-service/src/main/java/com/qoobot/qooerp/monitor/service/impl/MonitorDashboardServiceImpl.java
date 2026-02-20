package com.qoobot.qooerp.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.entity.MonitorAlert;
import com.qoobot.qooerp.monitor.entity.MonitorMetrics;
import com.qoobot.qooerp.monitor.entity.MonitorService;
import com.qoobot.qooerp.monitor.mapper.MonitorAlertMapper;
import com.qoobot.qooerp.monitor.mapper.MonitorMetricsMapper;
import com.qoobot.qooerp.monitor.mapper.MonitorServiceMapper;
import com.qoobot.qooerp.monitor.service.MonitorDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorDashboardServiceImpl implements MonitorDashboardService {

    private final MonitorServiceMapper serviceMapper;
    private final MonitorAlertMapper alertMapper;
    private final MonitorMetricsMapper metricsMapper;

    @Override
    public DashboardOverviewDTO getOverview() {
        DashboardOverviewDTO overview = new DashboardOverviewDTO();

        List<ServiceStatusDTO> servicesStatus = getServicesStatus();
        overview.setServicesStatus(servicesStatus);

        overview.setTotalServices(servicesStatus.size());
        overview.setOnlineServices((int) servicesStatus.stream().filter(s -> s.getStatus() == 1).count());
        overview.setOfflineServices((int) servicesStatus.stream().filter(s -> s.getStatus() == 0).count());
        overview.setUnhealthyServices((int) servicesStatus.stream().filter(s -> s.getHealthStatus() == 0).count());

        List<MonitorAlert> allAlerts = alertMapper.selectList(
                new LambdaQueryWrapper<MonitorAlert>()
                        .eq(MonitorAlert::getTenantId, TenantContextHolder.getTenantId())
        );

        overview.setTotalAlerts((long) allAlerts.size());
        overview.setPendingAlerts(allAlerts.stream().filter(a -> a.getStatus() == 0).count());
        overview.setHandledAlerts(allAlerts.stream().filter(a -> a.getStatus() == 1).count());
        overview.setClosedAlerts(allAlerts.stream().filter(a -> a.getStatus() == 2).count());

        List<MonitorAlert> recentAlerts = alertMapper.selectList(
                new LambdaQueryWrapper<MonitorAlert>()
                        .eq(MonitorAlert::getTenantId, TenantContextHolder.getTenantId())
                        .orderByDesc(MonitorAlert::getAlertTime)
                        .last("LIMIT 10")
        );
        overview.setRecentAlerts(recentAlerts.stream().map(this::convertAlertToDTO).collect(Collectors.toList()));

        DashboardMetricsDTO dashboardMetrics = new DashboardMetricsDTO();

        List<MonitorMetrics> metrics = metricsMapper.selectList(
                new LambdaQueryWrapper<MonitorMetrics>()
                        .eq(MonitorMetrics::getTenantId, TenantContextHolder.getTenantId())
                        .ge(MonitorMetrics::getTime, LocalDateTime.now().minusMinutes(5))
        );

        if (!metrics.isEmpty()) {
            Map<String, List<MonitorMetrics>> metricsByType = metrics.stream()
                    .collect(Collectors.groupingBy(MonitorMetrics::getMetricType));

            if (metricsByType.containsKey("cpu")) {
                List<MonitorMetrics> cpuMetrics = metricsByType.get("cpu");
                BigDecimal cpuAvg = cpuMetrics.stream()
                        .map(m -> BigDecimal.valueOf(m.getMetricValue()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(cpuMetrics.size()), 2, RoundingMode.HALF_UP);
                dashboardMetrics.setCpuUsage(cpuAvg);
            }

            if (metricsByType.containsKey("memory")) {
                List<MonitorMetrics> memoryMetrics = metricsByType.get("memory");
                BigDecimal memoryAvg = memoryMetrics.stream()
                        .map(m -> BigDecimal.valueOf(m.getMetricValue()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(memoryMetrics.size()), 2, RoundingMode.HALF_UP);
                dashboardMetrics.setMemoryUsage(memoryAvg);
            }
        }
        overview.setMetrics(dashboardMetrics);

        return overview;
    }

    @Override
    public List<ServiceStatusDTO> getServicesStatus() {
        List<MonitorService> services = serviceMapper.selectList(
                new LambdaQueryWrapper<MonitorService>()
                        .eq(MonitorService::getTenantId, TenantContextHolder.getTenantId())
        );

        return services.stream().map(s -> {
            ServiceStatusDTO dto = new ServiceStatusDTO();
            dto.setId(s.getId());
            dto.setServiceName(s.getServiceName());
            dto.setStatus(s.getStatus());
            dto.setHealthStatus(s.getHealthStatus());
            dto.setLastCheckTime(s.getLastCheckTime());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MonitorAlertDTO> getRealtimeAlerts() {
        List<MonitorAlert> alerts = alertMapper.selectList(
                new LambdaQueryWrapper<MonitorAlert>()
                        .eq(MonitorAlert::getTenantId, TenantContextHolder.getTenantId())
                        .eq(MonitorAlert::getStatus, 0)
                        .orderByDesc(MonitorAlert::getAlertTime)
                        .last("LIMIT 20")
        );

        return alerts.stream().map(this::convertAlertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<MetricsTrendDTO> getMetricsTrend(MetricsTrendQueryDTO dto) {
        MetricsTrendServiceImpl metricsTrendService = new MetricsTrendServiceImpl(metricsMapper);
        return metricsTrendService.getTrend(dto);
    }

    private MonitorAlertDTO convertAlertToDTO(MonitorAlert entity) {
        MonitorAlertDTO dto = new MonitorAlertDTO();
        dto.setId(entity.getId());
        dto.setServiceName(entity.getServiceName());
        dto.setAlertType(entity.getAlertType());
        dto.setAlertLevel(entity.getAlertLevel());
        dto.setAlertContent(entity.getAlertContent());
        dto.setAlertTime(entity.getAlertTime());
        dto.setStatus(entity.getStatus());
        dto.setHandlerId(entity.getHandlerId());
        dto.setHandleTime(entity.getHandleTime());
        dto.setHandleRemark(entity.getHandleRemark());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }

    private static class MetricsTrendServiceImpl {
        private final MonitorMetricsMapper metricsMapper;

        public MetricsTrendServiceImpl(MonitorMetricsMapper metricsMapper) {
            this.metricsMapper = metricsMapper;
        }

        public List<MetricsTrendDTO> getTrend(MetricsTrendQueryDTO dto) {
            LambdaQueryWrapper<MonitorMetrics> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MonitorMetrics::getTenantId, TenantContextHolder.getTenantId());
            wrapper.eq(MonitorMetrics::getServiceName, dto.getServiceName());
            wrapper.eq(MonitorMetrics::getMetricName, dto.getMetricName());
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

            return List.of(trendDTO);
        }
    }
}
