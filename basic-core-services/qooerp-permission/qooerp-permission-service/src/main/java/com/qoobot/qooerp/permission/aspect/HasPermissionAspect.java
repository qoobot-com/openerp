package com.qoobot.qooerp.permission.aspect;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.permission.annotation.HasPermission;
import com.qoobot.qooerp.permission.service.PermissionCompositeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限验证切面（支持@HasPermission注解）
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class HasPermissionAspect {

    private final PermissionCompositeService compositeService;

    @Around("@annotation(com.qoobot.qooerp.permission.annotation.HasPermission) || " +
            "@within(com.qoobot.qooerp.permission.annotation.HasPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        HasPermission classAnnotation = joinPoint.getTarget().getClass()
                .getAnnotation(HasPermission.class);
        HasPermission methodAnnotation = signature.getMethod()
                .getAnnotation(HasPermission.class);

        // 方法注解优先于类注解
        HasPermission annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;

        if (annotation != null && !annotation.skip()) {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                throw new BusinessException("未登录，请先登录");
            }

            // 检查RBAC权限
            String[] permissions = annotation.permissions().length > 0
                    ? annotation.permissions()
                    : new String[]{annotation.value()};

            if (permissions.length == 1 && "".equals(permissions[0])) {
                // 没有指定权限标识，只检查ABAC
                checkAbacPermission(userId, annotation, joinPoint.getArgs());
            } else {
                checkRbacPermission(userId, permissions, annotation.logical());
            }

            log.debug("权限验证通过: userId={}, method={}", userId, signature.getMethod().getName());
        }

        return joinPoint.proceed();
    }

    /**
     * 检查RBAC权限
     */
    private void checkRbacPermission(Long userId, String[] permissions, HasPermission.Logical logical) {
        boolean hasPermission = switch (logical) {
            case AND -> Arrays.stream(permissions)
                    .allMatch(p -> compositeService.checkPermission(userId, p, null));
            case OR -> Arrays.stream(permissions)
                    .anyMatch(p -> compositeService.checkPermission(userId, p, null));
        };

        if (!hasPermission) {
            throw new BusinessException("没有权限访问");
        }
    }

    /**
     * 检查ABAC权限
     */
    private void checkAbacPermission(Long userId, HasPermission annotation, Object[] args) {
        String resourceType = annotation.resourceType();
        String operationType = annotation.operationType();

        if (resourceType.isEmpty() || operationType.isEmpty()) {
            return; // 未指定ABAC参数，跳过检查
        }

        // 从方法参数中提取资源属性
        Map<String, Object> resourceAttributes = extractResourceAttributes(args);

        boolean hasPermission = compositeService.checkPermissionWithAbac(
                userId, "", resourceType, operationType, resourceAttributes);

        if (!hasPermission) {
            throw new BusinessException("没有权限访问该资源");
        }
    }

    /**
     * 从方法参数中提取资源属性
     */
    private Map<String, Object> extractResourceAttributes(Object[] args) {
        Map<String, Object> attributes = new HashMap<>();

        for (Object arg : args) {
            if (arg instanceof Map) {
                attributes.putAll((Map<String, Object>) arg);
            }
            // 可以根据实际需求提取更多类型的参数属性
        }

        return attributes;
    }
}
