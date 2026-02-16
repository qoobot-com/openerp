package com.qoobot.common.tenant;

/**
 * 租户上下文
 * 用于在当前线程中存储和获取租户ID
 */
public class TenantContextHolder {

    /**
     * 租户ID的线程本地变量
     */
    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    /**
     * 设置租户ID
     */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * 获取租户ID
     */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * 清除租户ID
     */
    public static void clear() {
        TENANT_ID.remove();
    }
}
