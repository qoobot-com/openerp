package com.qoobot.qooerp.organization.enums;

import lombok.Getter;

/**
 * 岗位状态枚举
 */
@Getter
public enum PositionStatus {

    /**
     * 禁用
     */
    DISABLED(0, "禁用"),

    /**
     * 启用
     */
    ENABLED(1, "启用");

    private final Integer code;
    private final String desc;

    PositionStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PositionStatus fromCode(Integer code) {
        for (PositionStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
