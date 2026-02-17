package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * OTLP Metric 数据 DTO
 * 解析后的指标数据
 */
@Data
public class OtlpMetricData {

    /**
     * 指标名称
     */
    private String metricName;

    /**
     * 指标类型（GAUGE, COUNTER, SUMMARY, HISTOGRAM）
     */
    private String metricType;

    /**
     * 指标值
     */
    private Double value;

    /**
     * 时间戳
     */
    private Instant timestamp;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 实例标识
     */
    private String instance;

    /**
     * 标签（tags/attributes）
     */
    private Map<String, String> tags;

    /**
     * 单位（可选）
     */
    private String unit;

    /**
     * 描述（可选）
     */
    private String description;

    // 用于 Histogram/Summary 类型
    /**
     * 分位数统计（适用于 SUMMARY 类型）
     */
    private Map<String, Double> quantiles;

    /**
     * 桶计数（适用于 HISTOGRAM 类型）
     */
    private Map<Double, Long> buckets;

    /**
     * 样本数量（适用于 HISTOGRAM/SUMMARY 类型）
     */
    private Long count;

    /**
     * 总和（适用于 HISTOGRAM/SUMMARY 类型）
     */
    private Double sum;
}
