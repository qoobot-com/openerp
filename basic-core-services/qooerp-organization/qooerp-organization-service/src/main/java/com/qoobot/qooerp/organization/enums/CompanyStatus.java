package com.qoobot.qooerp.organization.enums;

import lombok.Getter;

/**
 * 公司状态枚举
 */
@Getter
public enum CompanyStatus {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final Integer code;
    private final String desc;

    CompanyStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
