package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 健康检查实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_health_check")
public class SystemHealthCheck extends BaseEntity {

    /**
     * 检查ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 检查类型：SERVICE-服务 DATABASE-数据库 REDIS-缓存 MQ-消息队列
     */
    private String checkType;

    /**
     * 检查名称
     */
    private String checkName;

    /**
     * 检查状态：UP-正常 DOWN-异常 UNKNOWN-未知
     */
    private String status;

    /**
     * 响应时间（毫秒）
     */
    private Long responseTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 检查详情（JSON格式）
     */
    private String details;

    /**
     * 服务实例ID
     */
    private String instanceId;

    /**
     * 检查时间
     */
    private LocalDateTime checkTime;

    /**
     * 告警级别：INFO-信息 WARN-警告 ERROR-错误 CRITICAL-严重
     */
    private String alarmLevel;
}
