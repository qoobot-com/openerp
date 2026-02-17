package com.qoobot.qooerp.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日志状态枚举
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum LogStatusEnum {

    /**
     * 失败
     */
    FAIL(0, "失败"),

    /**
     * 成功
     */
    SUCCESS(1, "成功");

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
    public static LogStatusEnum fromValue(Integer value) {
        for (LogStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid log status value: " + value);
    }
}
