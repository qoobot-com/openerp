package com.qoobot.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典项VO
 */
@Data
public class DictItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典值
     */
    private String value;

    /**
     * 字典标签
     */
    private String label;

    /**
     * 字典类型
     */
    private String type;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否默认
     */
    private Boolean isDefault;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;
}
