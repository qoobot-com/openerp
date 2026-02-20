package com.qoobot.qooerp.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限范围枚举
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum {

    /**
     * 全部数据
     */
    ALL(1, "全部数据"),

    /**
     * 本部门数据
     */
    DEPT(2, "本部门数据"),

    /**
     * 本部门及子部门
     */
    DEPT_AND_CHILD(3, "本部门及子部门"),

    /**
     * 仅本人
     */
    SELF(4, "仅本人"),

    /**
     * 自定义数据
     */
    CUSTOM(5, "自定义数据");

    private final Integer code;
    private final String desc;

    public static DataScopeEnum getByCode(Integer code) {
        for (DataScopeEnum scope : values()) {
            if (scope.getCode().equals(code)) {
                return scope;
            }
        }
        return null;
    }
}
