package com.qoobot.qooerp.permission.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.permission.dto.PermissionUserDTO;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.service.PermissionUserService;
import com.qoobot.qooerp.permission.vo.PermissionMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户权限Controller
 */
@Tag(name = "用户权限管理")
@RestController
@RequestMapping("/api/permission/user")
@RequiredArgsConstructor
public class PermissionUserController {

    private final PermissionUserService userService;

    /**
     * 分配角色给用户
     */
    @Operation(summary = "分配角色给用户")
    @PostMapping("/roles")
    public Result<Boolean> assignRolesToUser(@Valid @RequestBody PermissionUserDTO dto) {
        Boolean result = userService.assignRolesToUser(dto);
        return Result.success(result);
    }

    /**
     * 删除用户的所有角色
     */
    @Operation(summary = "删除用户的所有角色")
    @DeleteMapping("/{userId}/roles")
    public Result<Boolean> deleteUserRoles(@PathVariable Long userId) {
        Boolean result = userService.deleteUserRoles(userId);
        return Result.success(result);
    }

    /**
     * 获取用户的角色列表
     */
    @Operation(summary = "获取用户的角色列表")
    @GetMapping("/{userId}/roles")
    public Result<List<PermissionRole>> getUserRoles(@PathVariable Long userId) {
        List<PermissionRole> roles = userService.getUserRoles(userId);
        return Result.success(roles);
    }

    /**
     * 获取用户的菜单树
     */
    @Operation(summary = "获取用户的菜单树")
    @GetMapping("/{userId}/menus")
    public Result<List<PermissionMenuVO>> getUserMenuTree(@PathVariable Long userId) {
        List<PermissionMenuVO> tree = userService.getUserMenuTree(userId);
        return Result.success(tree);
    }

    /**
     * 检查用户是否拥有指定权限
     */
    @Operation(summary = "检查用户权限")
    @GetMapping("/{userId}/check")
    public Result<Boolean> hasPermission(@PathVariable Long userId, @RequestParam String permission) {
        Boolean hasPerm = userService.hasPermission(userId, permission);
        return Result.success(hasPerm);
    }
}
