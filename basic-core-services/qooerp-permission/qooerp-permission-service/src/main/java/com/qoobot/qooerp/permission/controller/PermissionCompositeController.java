package com.qoobot.qooerp.permission.controller;

import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.permission.service.PermissionCompositeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * 复合权限控制器（RBAC + ABAC）
 */
@RestController
@RequestMapping("/api/permission/composite")
@RequiredArgsConstructor
public class PermissionCompositeController {

    private final PermissionCompositeService compositeService;

    /**
     * 验证权限（RBAC + ABAC组合）
     */
    @PostMapping("/check")
    public Result<Boolean> checkPermission(@RequestParam Long userId,
                                              @RequestParam String permission,
                                              @RequestBody(required = false) Map<String, Object> resourceAttributes) {
        return Result.success(compositeService.checkPermission(userId, permission, resourceAttributes));
    }

    /**
     * 验证权限（RBAC + ABAC组合，完整参数）
     */
    @PostMapping("/check-full")
    public Result<Boolean> checkPermissionWithAbac(
            @RequestParam Long userId,
            @RequestParam String permission,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String operationType,
            @RequestBody(required = false) Map<String, Object> resourceAttributes) {
        return Result.success(compositeService.checkPermissionWithAbac(
                userId, permission, resourceType, operationType, resourceAttributes));
    }

    /**
     * 获取用户的完整权限列表
     */
    @GetMapping("/user/{userId}/permissions")
    public Result<Set<String>> getUserPermissions(@PathVariable Long userId) {
        return Result.success(compositeService.getUserPermissions(userId));
    }

    /**
     * 判断是否超级管理员
     */
    @GetMapping("/user/{userId}/is-admin")
    public Result<Boolean> isSuperAdmin(@PathVariable Long userId) {
        return Result.success(compositeService.isSuperAdmin(userId));
    }

    /**
     * 设置权限评估模式
     */
    @PostMapping("/mode")
    public Result<Void> setPermissionMode(@RequestParam String mode) {
        compositeService.setPermissionMode(mode);
        return Result.success();
    }

    /**
     * 获取当前权限评估模式
     */
    @GetMapping("/mode")
    public Result<String> getPermissionMode() {
        return Result.success(compositeService.getPermissionMode());
    }
}
