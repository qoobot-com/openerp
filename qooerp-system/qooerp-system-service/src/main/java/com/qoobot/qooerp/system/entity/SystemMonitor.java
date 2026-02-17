package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统监控实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_monitor")
public class SystemMonitor extends BaseEntity {

    /**
     * 监控ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 监控类型：CPU/MEMORY/DISK/NETWORK/JVM/BUSINESS
     */
    private String monitorType;

    /**
     * 监控指标名称
     */
    private String metricName;

    /**
     * 监控指标值
     */
    private Double metricValue;

    /**
     * 监控指标单位
     */
    private String unit;

    /**
     * 阈值（用于告警）
     */
    private Double threshold;

    /**
     * 状态：NORMAL-正常 WARNING-警告 CRITICAL-严重
     */
    private String status;

    /**
     * 服务实例ID
     */
    private String instanceId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 监控时间
     */
    private LocalDateTime monitorTime;

    /**
     * 额外信息（JSON格式）
     */
    private String extraInfo;
}
