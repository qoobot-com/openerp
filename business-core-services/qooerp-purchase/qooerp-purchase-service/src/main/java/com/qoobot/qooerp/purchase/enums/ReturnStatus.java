package com.qoobot.qooerp.purchase.enums;

public enum ReturnStatus {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已审核"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String desc;

    ReturnStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
