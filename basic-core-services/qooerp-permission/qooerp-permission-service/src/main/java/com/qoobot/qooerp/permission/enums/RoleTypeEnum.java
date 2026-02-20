package com.qoobot.qooerp.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色类型枚举
 */
@Getter
@AllArgsConstructor
public enum RoleTypeEnum {

    /**
     * 系统角色
     */
    SYSTEM(1, "系统角色"),

    /**
     * 业务角色
     */
    BUSINESS(2, "业务角色");

    private final Integer code;
    private final String desc;

    public static RoleTypeEnum getByCode(Integer code) {
        for (RoleTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
