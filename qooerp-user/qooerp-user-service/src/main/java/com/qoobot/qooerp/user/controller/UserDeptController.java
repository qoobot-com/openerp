package com.qoobot.qooerp.user.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.user.service.UserDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户部门控制器
 */
@RestController
@RequestMapping("/api/user/{userId}/depts")
@RequiredArgsConstructor
public class UserDeptController {

    private final UserDeptService userDeptService;

    /**
     * 分配部门
     */
    @PostMapping
    public Result<Void> assignDepts(@PathVariable Long userId, @RequestBody Map<String, List<Long>> request) {
        List<Long> deptIds = request.get("deptIds");
        boolean success = userDeptService.assignDepts(userId, deptIds);
        return Result.success();
    }

    /**
     * 设置主部门
     */
    @PutMapping("/primary/{deptId}")
    public Result<Void> setPrimaryDept(@PathVariable Long userId, @PathVariable Long deptId) {
        boolean success = userDeptService.setPrimaryDept(userId, deptId);
        return Result.success();
    }

    /**
     * 移除部门
     */
    @DeleteMapping
    public Result<Void> removeDepts(@PathVariable Long userId, @RequestBody Map<String, List<Long>> request) {
        List<Long> deptIds = request.get("deptIds");
        boolean success = userDeptService.removeDepts(userId, deptIds);
        return Result.success();
    }

    /**
     * 更新用户部门
     */
    @PutMapping
    public Result<Void> updateDepts(@PathVariable Long userId, @RequestBody Map<String, List<Long>> request) {
        List<Long> deptIds = request.get("deptIds");
        boolean success = userDeptService.updateDepts(userId, deptIds);
        return Result.success();
    }

    /**
     * 清空用户部门
     */
    @DeleteMapping("/all")
    public Result<Void> clearDepts(@PathVariable Long userId) {
        boolean success = userDeptService.clearDepts(userId);
        return Result.success();
    }

    /**
     * 获取用户部门ID列表
     */
    @GetMapping
    public Result<List<Long>> getDeptIds(@PathVariable Long userId) {
        List<Long> deptIds = userDeptService.getDeptIds(userId);
        return Result.success(deptIds);
    }

    /**
     * 获取用户主部门
     */
    @GetMapping("/primary")
    public Result<Long> getPrimaryDept(@PathVariable Long userId) {
        Long deptId = userDeptService.getPrimaryDept(userId);
        return Result.success(deptId);
    }

    /**
     * 批量分配部门
     */
    @PostMapping("/batch")
    public Result<Void> batchAssignDepts(@RequestBody Map<String, Object> request) {
        List<Long> userIds = (List<Long>) request.get("userIds");
        List<Long> deptIds = (List<Long>) request.get("deptIds");
        boolean success = userDeptService.batchAssignDepts(userIds, deptIds);
        return Result.success();
    }

    /**
     * 批量移除部门
     */
    @DeleteMapping("/batch")
    public Result<Void> batchRemoveDepts(@RequestBody Map<String, Object> request) {
        List<Long> userIds = (List<Long>) request.get("userIds");
        List<Long> deptIds = (List<Long>) request.get("deptIds");
        boolean success = userDeptService.batchRemoveDepts(userIds, deptIds);
        return Result.success();
    }
}
