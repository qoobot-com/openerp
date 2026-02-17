package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.util.Map;

/**
 * Prometheus 格式指标数据 DTO
 */
@Data
public class PrometheusMetricData {

    /**
     * 指标名称
     */
    private String name;

    /**
     * 指标类型（gauge, counter, summary, histogram）
     */
    private String type;

    /**
     * 指标值
     */
    private Double value;

    /**
     * 时间戳（毫秒）
     */
    private Long timestamp;

    /**
     * 标签
     */
    private Map<String, String> labels;

    /**
     * 描述（可选）
     */
    private String help;

    /**
     * 单位（可选）
     */
    private String unit;
}
