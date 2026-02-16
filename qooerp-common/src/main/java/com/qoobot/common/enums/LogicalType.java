package com.qoobot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 逻辑类型枚举
 */
@Getter
@AllArgsConstructor
public enum LogicalType {

    AND("并且"),
    OR("或者");

    private final String description;
}
