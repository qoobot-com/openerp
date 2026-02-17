package com.qoobot.qooerp.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.monitor.dto.*;

import java.util.List;

public interface MonitorMetricsService {
    MonitorMetricsDTO getCurrentMetrics(String serviceName);

    Page<MonitorMetricsDTO> getHistoryMetrics(MetricsQueryDTO dto);

    MetricsSummaryDTO getSummary(MetricsSummaryQueryDTO dto);

    List<MetricsTrendDTO> getTrend(MetricsTrendQueryDTO dto);
}
