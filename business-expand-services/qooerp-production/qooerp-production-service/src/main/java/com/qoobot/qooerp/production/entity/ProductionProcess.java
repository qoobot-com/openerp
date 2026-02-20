package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 生产工序实体
 */
@Data
@TableName("production_process")
public class ProductionProcess {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String processCode;

    private String processName;

    private Long productId;

    private String processType;

    private BigDecimal standardTime;

    private Long equipmentId;

    private String equipmentName;

    private Integer processOrder;

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
