package com.qoobot.qooerp.permission.aspect;

import com.qoobot.qooerp.permission.enums.DataScopeEnum;

/**
 * 数据权限上下文持有者
 * 用于存储当前请求的数据权限范围
 */
public class DataScopeContextHolder {

    private static final ThreadLocal<DataScopeEnum> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据权限范围
     */
    public static void set(DataScopeEnum dataScope) {
        CONTEXT_HOLDER.set(dataScope);
    }

    /**
     * 获取数据权限范围
     */
    public static DataScopeEnum get() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除数据权限范围
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 检查是否需要数据权限过滤
     */
    public static boolean needFilter() {
        return CONTEXT_HOLDER.get() != null;
    }
}
