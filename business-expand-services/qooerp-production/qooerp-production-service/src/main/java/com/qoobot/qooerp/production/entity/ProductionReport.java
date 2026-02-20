package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产报工实体
 */
@Data
@TableName("production_report")
public class ProductionReport {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String reportNo;

    private Long orderId;

    private String orderNo;

    private Long processId;

    private String processName;

    private Long productId;

    private String productName;

    private BigDecimal reportQuantity;

    private BigDecimal qualifiedQuantity;

    private BigDecimal rejectQuantity;

    private BigDecimal workTime;

    private Long workerId;

    private String workerName;

    private Long equipmentId;

    private String equipmentName;

    private LocalDate reportDate;

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
