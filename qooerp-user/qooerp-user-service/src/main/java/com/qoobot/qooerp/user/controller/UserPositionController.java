package com.qoobot.qooerp.user.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.user.service.UserPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户岗位控制器
 */
@RestController
@RequestMapping("/api/user/{userId}/positions")
@RequiredArgsConstructor
public class UserPositionController {

    private final UserPositionService userPositionService;

    /**
     * 分配岗位
     */
    @PostMapping
    public Result<Void> assignPositions(@PathVariable Long userId, @RequestBody Map<String, List<Long>> request) {
        List<Long> positionIds = request.get("positionIds");
        boolean success = userPositionService.assignPositions(userId, positionIds);
        return Result.success();
    }

    /**
     * 移除岗位
     */
    @DeleteMapping
    public Result<Void> removePositions(@PathVariable Long userId, @RequestBody Map<String, List<Long>> request) {
        List<Long> positionIds = request.get("positionIds");
        boolean success = userPositionService.removePositions(userId, positionIds);
        return Result.success();
    }

    /**
     * 更新用户岗位
     */
    @PutMapping
    public Result<Void> updatePositions(@PathVariable Long userId, @RequestBody Map<String, List<Long>> request) {
        List<Long> positionIds = request.get("positionIds");
        boolean success = userPositionService.updatePositions(userId, positionIds);
        return Result.success();
    }

    /**
     * 清空用户岗位
     */
    @DeleteMapping("/all")
    public Result<Void> clearPositions(@PathVariable Long userId) {
        boolean success = userPositionService.clearPositions(userId);
        return Result.success();
    }

    /**
     * 获取用户岗位ID列表
     */
    @GetMapping
    public Result<List<Long>> getPositionIds(@PathVariable Long userId) {
        List<Long> positionIds = userPositionService.getPositionIds(userId);
        return Result.success(positionIds);
    }

    /**
     * 批量分配岗位
     */
    @PostMapping("/batch")
    public Result<Void> batchAssignPositions(@RequestBody Map<String, Object> request) {
        List<Long> userIds = (List<Long>) request.get("userIds");
        List<Long> positionIds = (List<Long>) request.get("positionIds");
        boolean success = userPositionService.batchAssignPositions(userIds, positionIds);
        return Result.success();
    }

    /**
     * 批量移除岗位
     */
    @DeleteMapping("/batch")
    public Result<Void> batchRemovePositions(@RequestBody Map<String, Object> request) {
        List<Long> userIds = (List<Long>) request.get("userIds");
        List<Long> positionIds = (List<Long>) request.get("positionIds");
        boolean success = userPositionService.batchRemovePositions(userIds, positionIds);
        return Result.success();
    }
}
