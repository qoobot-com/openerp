package com.qoobot.qooerp.common.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

/**
 * 多租户拦截器
 * 自动在SQL中注入租户ID
 */
@Slf4j
public class TenantInterceptor implements TenantLineHandler {

    /**
     * 获取租户ID
     */
    @Override
    public Expression getTenantId() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            return new LongValue(-1L);  // 默认租户ID
        }
        return new LongValue(tenantId);
    }

    /**
     * 获取租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    /**
     * 判断表是否需要过滤租户
     */
    @Override
    public boolean ignoreTable(String tableName) {
        // 系统表不需要过滤租户
        String[] ignoreTables = {
            "sys_user", "sys_role", "sys_menu", "sys_dept",
            "sys_dict", "sys_config", "sys_log"
        };
        for (String table : ignoreTables) {
            if (table.equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }
}
