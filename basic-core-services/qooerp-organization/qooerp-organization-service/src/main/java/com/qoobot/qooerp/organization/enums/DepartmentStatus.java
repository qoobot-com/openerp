package com.qoobot.qooerp.organization.enums;

import lombok.Getter;

/**
 * 部门状态枚举
 */
@Getter
public enum DepartmentStatus {

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

    DepartmentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DepartmentStatus fromCode(Integer code) {
        for (DepartmentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
