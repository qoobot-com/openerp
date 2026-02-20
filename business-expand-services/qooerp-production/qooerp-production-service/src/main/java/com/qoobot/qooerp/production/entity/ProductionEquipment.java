package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产设备实体
 */
@Data
@TableName("production_equipment")
public class ProductionEquipment {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String equipmentCode;

    private String equipmentName;

    private String equipmentType;

    private Long workshopId;

    private String workshopName;

    private String specification;

    private String manufacturer;

    private LocalDate purchaseDate;

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
