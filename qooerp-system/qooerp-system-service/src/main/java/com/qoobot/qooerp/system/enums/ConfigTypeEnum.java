package com.qoobot.qooerp.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置参数类型枚举
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ConfigTypeEnum {

    /**
     * 字符串
     */
    STRING("string", "字符串"),

    /**
     * 数字
     */
    NUMBER("number", "数字"),

    /**
     * 布尔值
     */
    BOOLEAN("boolean", "布尔值");

    /**
     * 类型值
     */
    private final String value;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     */
    public static ConfigTypeEnum fromValue(String value) {
        for (ConfigTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid config type value: " + value);
    }
}
