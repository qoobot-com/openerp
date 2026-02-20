package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产报废实体
 */
@Data
@TableName("production_scrap")
public class ProductionScrap {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String scrapNo;

    private Long orderId;

    private String orderNo;

    private Long reportId;

    private Long productId;

    private String productName;

    private String productCode;

    private BigDecimal scrapQuantity;

    private String scrapReason;

    private String scrapType;

    private Long processId;

    private String processName;

    private LocalDate scrapDate;

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
