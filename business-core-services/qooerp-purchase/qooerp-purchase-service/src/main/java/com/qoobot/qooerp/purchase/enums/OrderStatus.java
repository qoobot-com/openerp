package com.qoobot.qooerp.purchase.enums;

public enum OrderStatus {
    DRAFT("DRAFT", "草稿"),
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已审核"),
    RECEIVING("RECEIVING", "收货中"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String desc;

    OrderStatus(String code, String desc) {
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
