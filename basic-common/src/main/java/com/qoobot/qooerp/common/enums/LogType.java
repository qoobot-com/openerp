package com.qoobot.qooerp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日志类型枚举
 */
@Getter
@AllArgsConstructor
public enum LogType {

    INSERT("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    QUERY("查询"),
    EXPORT("导出"),
    IMPORT("导入"),
    LOGIN("登录"),
    LOGOUT("登出"),
    OTHER("其他");

    private final String description;
}
