package com.qoobot.qooerp.permission.aspect;

import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.permission.annotation.DataScope;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.enums.DataScopeEnum;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限切面
 * 用于在方法执行前设置数据权限范围
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {

    private final PermissionRoleService roleService;

    /**
     * 数据权限拦截
     */
    @Around("@annotation(com.qoobot.qooerp.permission.annotation.DataScope)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            // 未登录用户，不进行数据权限过滤
            return point.proceed();
        }

        // 获取注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DataScope dataScope = method.getAnnotation(DataScope.class);

        if (dataScope == null || !dataScope.enabled()) {
            return point.proceed();
        }

        // 获取用户角色
        List<PermissionRole> roles = roleService.getRolesByUserId(userId);
        if (roles.isEmpty()) {
            // 没有角色，不进行数据权限过滤
            return point.proceed();
        }

        // 检查是否是超级管理员
        boolean isAdmin = roles.stream().anyMatch(role ->
                "ADMIN".equals(role.getRoleCode()));
        if (isAdmin) {
            // 超级管理员查看所有数据
            return point.proceed();
        }

        // 获取用户的数据权限范围（取最小权限）
        DataScopeEnum minScope = roles.stream()
                .map(role -> DataScopeEnum.getByCode(role.getDataScope()))
                .filter(scope -> scope != null)
                .min((s1, s2) -> s1.getCode().compareTo(s2.getCode()))
                .orElse(DataScopeEnum.SELF);

        // 设置数据权限范围到ThreadLocal
        DataScopeContextHolder.set(minScope);

        try {
            return point.proceed();
        } finally {
            // 清除ThreadLocal
            DataScopeContextHolder.clear();
        }
    }
}
