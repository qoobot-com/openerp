package com.qoobot.qooerp.common.tenant;

import com.qoobot.qooerp.common.security.AuthUserDetails;
import com.qoobot.qooerp.common.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 租户上下文持有者
 * 从Spring Security上下文中获取租户ID,确保在异步场景下也能正确获取
 *
 * @author QooERP Team
 * @version 2.0.0
 */
public class TenantContextHolder {

    /**
     * 租户ID的线程本地变量(备用方案,用于非Spring Security环境)
     */
    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    /**
     * 获取租户ID
     * 优先从Spring Security上下文获取,如果不存在则从ThreadLocal获取
     *
     * @return 租户ID,如果不存在则返回null
     */
    public static Long getTenantId() {
        // 优先从Spring Security上下文获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUserDetails) {
            AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();
            Long tenantId = userDetails.getTenantId();
            if (tenantId != null) {
                return tenantId;
            }
        }

        // 如果Spring Security上下文中没有,从ThreadLocal获取
        return TENANT_ID.get();
    }

    /**
     * 获取租户ID,如果不存在则返回默认值
     *
     * @param defaultTenantId 默认租户ID
     * @return 租户ID
     */
    public static Long getTenantIdOrDefault(Long defaultTenantId) {
        Long tenantId = getTenantId();
        return tenantId != null ? tenantId : defaultTenantId;
    }

    /**
     * 设置租户ID到线程本地变量
     * 注意:通常不需要手动调用,租户ID会自动从JWT Token中解析并存储在SecurityContext中
     * 此方法仅用于特殊情况(如异步任务传递租户上下文)
     *
     * @param tenantId 租户ID
     */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * 清除线程本地变量中的租户ID
     * 建议在请求结束后调用,避免内存泄漏
     */
    public static void clear() {
        TENANT_ID.remove();
    }

    /**
     * 判断当前是否存在租户上下文
     *
     * @return true表示存在租户上下文,false表示不存在
     */
    public static boolean hasTenant() {
        return getTenantId() != null;
    }
}
