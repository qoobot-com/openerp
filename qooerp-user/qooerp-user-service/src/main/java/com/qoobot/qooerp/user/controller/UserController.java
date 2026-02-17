package com.qoobot.qooerp.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.user.entity.UserInfo;
import com.qoobot.qooerp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    public Result<Long> createUser(@RequestBody UserInfo userInfo) {
        Long userId = userService.createUser(userInfo);
        return Result.success(userId);
    }

    /**
     * 更新用户
     */
    @PutMapping
    public Result<Void> updateUser(@RequestBody UserInfo userInfo) {
        boolean success = userService.updateUser(userInfo);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        boolean success = userService.deleteUser(userId);
        return Result.success();
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> userIds) {
        boolean success = userService.batchDeleteUsers(userIds);
        return Result.success();
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    public Result<UserInfo> getUserById(@PathVariable Long userId) {
        UserInfo user = userService.getUserById(userId);
        return Result.success(user);
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public Result<UserInfo> getUserByUsername(@PathVariable String username) {
        UserInfo user = userService.getUserByUsername(username);
        return Result.success(user);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<IPage<UserInfo>> pageUsers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        IPage<UserInfo> page = userService.pageUsers(current, size, username, phone,
                email, realName, status, startTime, endTime);
        return Result.success(page);
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public Result<List<UserInfo>> searchUsers(@RequestParam String keyword) {
        List<UserInfo> users = userService.searchUsers(keyword);
        return Result.success(users);
    }

    /**
     * 批量获取用户
     */
    @PostMapping("/batch")
    public Result<List<UserInfo>> getUsersByIds(@RequestBody List<Long> userIds) {
        List<UserInfo> users = userService.getUsersByIds(userIds);
        return Result.success(users);
    }

    /**
     * 启用用户
     */
    @PutMapping("/{userId}/enable")
    public Result<Void> enableUser(@PathVariable Long userId) {
        boolean success = userService.enableUser(userId);
        return Result.success();
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{userId}/disable")
    public Result<Void> disableUser(@PathVariable Long userId) {
        boolean success = userService.disableUser(userId);
        return Result.success();
    }

    /**
     * 锁定用户
     */
    @PutMapping("/{userId}/lock")
    public Result<Void> lockUser(@PathVariable Long userId) {
        boolean success = userService.lockUser(userId);
        return Result.success();
    }

    /**
     * 解锁用户
     */
    @PutMapping("/{userId}/unlock")
    public Result<Void> unlockUser(@PathVariable Long userId) {
        boolean success = userService.unlockUser(userId);
        return Result.success();
    }

    /**
     * 批量启用用户
     */
    @PutMapping("/batch/enable")
    public Result<Void> batchEnableUsers(@RequestBody List<Long> userIds) {
        boolean success = userService.batchEnableUsers(userIds);
        return Result.success();
    }

    /**
     * 批量禁用用户
     */
    @PutMapping("/batch/disable")
    public Result<Void> batchDisableUsers(@RequestBody List<Long> userIds) {
        boolean success = userService.batchDisableUsers(userIds);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/{userId}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        boolean success = userService.resetPassword(userId, newPassword);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/{userId}/change-password")
    public Result<Void> changePassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        boolean success = userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    /**
     * 获取用户角色列表
     */
    @GetMapping("/{userId}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        List<Long> roleIds = userService.getUserRoleIds(userId);
        return Result.success(roleIds);
    }

    /**
     * 获取用户部门列表
     */
    @GetMapping("/{userId}/depts")
    public Result<List<Long>> getUserDepts(@PathVariable Long userId) {
        List<Long> deptIds = userService.getUserDeptIds(userId);
        return Result.success(deptIds);
    }

    /**
     * 获取用户主部门
     */
    @GetMapping("/{userId}/primary-dept")
    public Result<Long> getUserPrimaryDept(@PathVariable Long userId) {
        Long deptId = userService.getUserPrimaryDept(userId);
        return Result.success(deptId);
    }

    /**
     * 获取用户岗位列表
     */
    @GetMapping("/{userId}/positions")
    public Result<List<Long>> getUserPositions(@PathVariable Long userId) {
        List<Long> positionIds = userService.getUserPositionIds(userId);
        return Result.success(positionIds);
    }

    /**
     * 获取用户完整信息
     */
    @GetMapping("/{userId}/full-info")
    public Result<Map<String, Object>> getUserFullInfo(@PathVariable Long userId) {
        Map<String, Object> fullInfo = userService.getUserFullInfo(userId);
        return Result.success(fullInfo);
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check/username")
    public Result<Boolean> checkUsernameExists(@RequestParam String username) {
        boolean exists = userService.checkUsernameExists(username);
        return Result.success(exists);
    }

    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check/phone")
    public Result<Boolean> checkPhoneExists(@RequestParam String phone) {
        boolean exists = userService.checkPhoneExists(phone);
        return Result.success(exists);
    }

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check/email")
    public Result<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.checkEmailExists(email);
        return Result.success(exists);
    }
}
