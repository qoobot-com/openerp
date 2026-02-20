package com.qoobot.qooerp.report.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仪表盘类型枚举
 *
 * @author Auto
 * @since 2026-02-18
 */
@Getter
@AllArgsConstructor
public enum DashboardType {

    /**
     * 个人仪表盘
     */
    PERSONAL(1, "个人仪表盘"),

    /**
     * 公共仪表盘
     */
    PUBLIC(2, "公共仪表盘"),

    /**
     * 系统仪表盘
     */
    SYSTEM(3, "系统仪表盘");

    private final Integer code;
    private final String desc;

    public static DashboardType getByCode(Integer code) {
        for (DashboardType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
