package com.qoobot.qooerp.notify.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 通知类型枚举
 */
public enum NotifyTypeEnum {

    EMAIL("email", "邮件"),
    SMS("sms", "短信"),
    PUSH("push", "推送"),
    INTERNAL("internal", "站内"),
    WEBHOOK("webhook", "Webhook");

    @EnumValue
    private final String value;

    private final String desc;

    NotifyTypeEnum(String value, String desc) {
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
