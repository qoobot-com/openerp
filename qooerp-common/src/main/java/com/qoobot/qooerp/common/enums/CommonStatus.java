package com.qoobot.qooerp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 */
@Getter
@AllArgsConstructor
public enum CommonStatus {

    ENABLED(1, "启用"),
    DISABLED(0, "禁用"),
    REGULAR(2, "转正"),
    RESIGNED(3, "离职");

    private final Integer code;
    private final String description;

    public static CommonStatus fromCode(Integer code) {
        for (CommonStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return DISABLED;
    }
}
