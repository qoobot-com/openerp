package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 字典项表实体
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_dict_item")
public class SystemDictItem {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典ID
     */
    @TableField("dict_id")
    private Long dictId;

    /**
     * 字典项标签
     */
    @TableField("item_label")
    private String itemLabel;

    /**
     * 字典项值
     */
    @TableField("item_value")
    private String itemValue;

    /**
     * 字典项编码
     */
    @TableField("item_code")
    private String itemCode;

    /**
     * 排序号
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
