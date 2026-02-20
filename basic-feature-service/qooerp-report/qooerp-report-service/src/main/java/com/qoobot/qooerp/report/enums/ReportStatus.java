package com.qoobot.qooerp.report.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 报表状态枚举
 *
 * @author Auto
 * @since 2026-02-18
 */
@Getter
@AllArgsConstructor
public enum ReportStatus {

    /**
     * 草稿
     */
    DRAFT(0, "草稿"),

    /**
     * 已发布
     */
    PUBLISHED(1, "已发布"),

    /**
     * 已归档
     */
    ARCHIVED(2, "已归档");

    private final Integer code;
    private final String desc;

    public static ReportStatus getByCode(Integer code) {
        for (ReportStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
