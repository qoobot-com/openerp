package com.qoobot.qooerp.production.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductionScheduleVO {
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
    private String statusDesc;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}
