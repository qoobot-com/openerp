package com.qoobot.qooerp.permission.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.permission.service.PermissionService;
import com.qoobot.qooerp.permission.vo.PermissionMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限Controller
 */
@Tag(name = "权限验证")
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 验证用户权限
     */
    @Operation(summary = "验证用户权限")
    @GetMapping("/check")
    public Result<Boolean> checkPermission(@RequestParam Long userId, @RequestParam String permission) {
        Boolean hasPerm = permissionService.hasPermission(userId, permission);
        return Result.success(hasPerm);
    }

    /**
     * 获取当前用户权限标识列表
     */
    @Operation(summary = "获取当前用户权限列表")
    @GetMapping("/permissions")
    public Result<List<String>> getCurrentUserPermissions() {
        List<String> permissions = permissionService.getCurrentUserPermissions();
        return Result.success(permissions);
    }

    /**
     * 获取当前用户菜单树
     */
    @Operation(summary = "获取当前用户菜单树")
    @GetMapping("/menu/tree")
    public Result<List<PermissionMenuVO>> getCurrentUserMenuTree() {
        List<PermissionMenuVO> tree = permissionService.getCurrentUserMenuTree();
        return Result.success(tree);
    }

    /**
     * 刷新用户权限缓存
     */
    @Operation(summary = "刷新用户权限缓存")
    @PostMapping("/refresh/{userId}")
    public Result<Void> refreshUserCache(@PathVariable Long userId) {
        permissionService.refreshUserCache(userId);
        return Result.success();
    }

    /**
     * 清除所有权限缓存
     */
    @Operation(summary = "清除所有权限缓存")
    @PostMapping("/cache/clear")
    public Result<Void> clearAllCache() {
        permissionService.clearAllCache();
        return Result.success();
    }
}
