package com.qoobot.qooerp.notify.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 通知状态枚举
 */
public enum NotifyStatusEnum {

    PENDING("PENDING", "待发送"),
    SENDING("SENDING", "发送中"),
    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败");

    @EnumValue
    private final String value;

    private final String desc;

    NotifyStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
