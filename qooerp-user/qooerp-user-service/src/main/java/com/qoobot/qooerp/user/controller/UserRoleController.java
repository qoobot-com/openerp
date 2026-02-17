package com.qoobot.qooerp.user.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户角色控制器
 */
@RestController
@RequestMapping("/api/user/{userId}/roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    /**
     * 分配角色
     */
    @PostMapping
    public Result<Void> assignRoles(@PathVariable Long userId, @RequestBody Map<String, List<Long>> request) {
        List<Long> roleIds = request.get("roleIds");
        boolean success = userRoleService.assignRoles(userId, roleIds);
        return Result.success();
    }

    /**
     * 移除角色
     */
    @DeleteMapping
    public Result<Void> removeRoles(@PathVariable Long userId, @RequestBody Map<String, List<Long>> request) {
        List<Long> roleIds = request.get("roleIds");
        boolean success = userRoleService.removeRoles(userId, roleIds);
        return Result.success();
    }

    /**
     * 更新用户角色
     */
    @PutMapping
    public Result<Void> updateRoles(@PathVariable Long userId, @RequestBody Map<String, List<Long>> request) {
        List<Long> roleIds = request.get("roleIds");
        boolean success = userRoleService.updateRoles(userId, roleIds);
        return Result.success();
    }

    /**
     * 清空用户角色
     */
    @DeleteMapping("/all")
    public Result<Void> clearRoles(@PathVariable Long userId) {
        boolean success = userRoleService.clearRoles(userId);
        return Result.success();
    }

    /**
     * 获取用户角色ID列表
     */
    @GetMapping
    public Result<List<Long>> getRoleIds(@PathVariable Long userId) {
        List<Long> roleIds = userRoleService.getRoleIds(userId);
        return Result.success(roleIds);
    }

    /**
     * 批量分配角色
     */
    @PostMapping("/batch")
    public Result<Void> batchAssignRoles(@RequestBody Map<String, Object> request) {
        List<Long> userIds = (List<Long>) request.get("userIds");
        List<Long> roleIds = (List<Long>) request.get("roleIds");
        boolean success = userRoleService.batchAssignRoles(userIds, roleIds);
        return Result.success();
    }

    /**
     * 批量移除角色
     */
    @DeleteMapping("/batch")
    public Result<Void> batchRemoveRoles(@RequestBody Map<String, Object> request) {
        List<Long> userIds = (List<Long>) request.get("userIds");
        List<Long> roleIds = (List<Long>) request.get("roleIds");
        boolean success = userRoleService.batchRemoveRoles(userIds, roleIds);
        return Result.success();
    }
}
