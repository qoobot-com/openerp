package com.qoobot.qooerp.notify.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 模板类型枚举
 */
public enum TemplateTypeEnum {

    EMAIL("email", "邮件"),
    SMS("sms", "短信"),
    PUSH("push", "推送"),
    INTERNAL("internal", "站内"),
    WEBHOOK("webhook", "Webhook");

    @EnumValue
    private final String value;

    private final String desc;

    TemplateTypeEnum(String value, String desc) {
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
