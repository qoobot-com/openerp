package com.qoobot.qooerp.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典状态枚举
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DictStatusEnum {

    /**
     * 禁用
     */
    DISABLE(0, "禁用"),

    /**
     * 启用
     */
    ENABLE(1, "启用");

    /**
     * 状态值
     */
    private final Integer value;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     */
    public static DictStatusEnum fromValue(Integer value) {
        for (DictStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid dict status value: " + value);
    }
}
