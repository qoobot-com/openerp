package com.qoobot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum OperationType {

    INSERT("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    QUERY("查询"),
    EXPORT("导出"),
    IMPORT("导入"),
    OTHER("其他");

    private final String description;

    public static OperationType fromName(String name) {
        for (OperationType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return OTHER;
    }
}
