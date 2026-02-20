package com.qoobot.qooerp.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 低代码表单实体
 */
@Data
@TableName("lowcode_form")
public class LowcodeForm {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单编码
     */
    private String formCode;

    /**
     * 表单配置JSON
     */
    private String formData;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 状态: 1-草稿 2-发布 3-下架
     */
    private Integer status;

    /**
     * 表单描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 分区字段
     */
    private Integer bizDate;
}
