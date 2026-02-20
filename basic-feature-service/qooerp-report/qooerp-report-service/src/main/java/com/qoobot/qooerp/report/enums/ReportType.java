package com.qoobot.qooerp.report.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 报表类型枚举
 *
 * @author Auto
 * @since 2026-02-18
 */
@Getter
@AllArgsConstructor
public enum ReportType {

    /**
     * 列表报表
     */
    LIST(1, "列表报表"),

    /**
     * 图表报表
     */
    CHART(2, "图表报表"),

    /**
     * 混合报表
     */
    MIXED(3, "混合报表"),

    /**
     * 交叉报表
     */
    CROSS(4, "交叉报表"),

    /**
     * 透视报表
     */
    PIVOT(5, "透视报表");

    private final Integer code;
    private final String desc;

    public static ReportType getByCode(Integer code) {
        for (ReportType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
