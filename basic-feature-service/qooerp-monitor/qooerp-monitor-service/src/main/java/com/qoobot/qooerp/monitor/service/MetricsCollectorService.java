package com.qoobot.qooerp.monitor.service;

import com.qoobot.qooerp.monitor.dto.MetricsTrendQueryDTO;

/**
 * Metrics 采集服务接口
 * 负责 Micrometer 指标数据的采集与存储
 */
public interface MetricsCollectorService {

    /**
     * 接收推送的指标数据（JSON 格式）
     *
     * @param metricsData 指标数据 JSON 字符串
     */
    void pushMetrics(String metricsData);

    /**
     * 批量接收推送的指标数据
     *
     * @param metricsDataList 指标数据列表
     */
    void batchPushMetrics(String[] metricsDataList);

    /**
     * 获取 Prometheus 格式的指标数据（用于 Scrape）
     *
     * @param serviceName 服务名称
     * @return Prometheus 文本格式（text/plain）
     */
    String scrapeMetrics(String serviceName);

    /**
     * 拉取指定服务的实时指标
     *
     * @param serviceName 服务名称
     * @param timeRange 时间范围（1h, 6h, 24h）
     * @return 指标数据 JSON 字符串
     */
    String pullRealtimeMetrics(String serviceName, String timeRange);
}
