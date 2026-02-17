package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统参数表实体
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_config")
public class SystemConfig {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 参数名称
     */
    @TableField("config_name")
    private String configName;

    /**
     * 参数键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 参数值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 参数类型：string/number/boolean
     */
    @TableField("config_type")
    private String configType;

    /**
     * 是否系统参数：0-否 1-是
     */
    @TableField("is_system")
    private Integer isSystem;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}
