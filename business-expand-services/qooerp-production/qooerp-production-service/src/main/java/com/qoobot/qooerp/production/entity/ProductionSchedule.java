package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 生产排程实体
 */
@Data
@TableName("production_schedule")
public class ProductionSchedule {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String scheduleCode;

    private String scheduleName;

    private Long orderId;

    private String orderNo;

    private Long processId;

    private String processName;

    private Long equipmentId;

    private String equipmentName;

    private Long workerId;

    private String workerName;

    private LocalDateTime plannedStartTime;

    private LocalDateTime plannedEndTime;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;

    private String status;

    private String remark;

    private Long tenantId;

    private String createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    private String updatedBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
