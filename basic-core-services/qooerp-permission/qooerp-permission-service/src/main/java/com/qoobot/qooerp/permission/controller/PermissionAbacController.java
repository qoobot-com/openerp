package com.qoobot.qooerp.permission.controller;

import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.permission.entity.PermissionAbacAttribute;
import com.qoobot.qooerp.permission.entity.PermissionAbacPolicy;
import com.qoobot.qooerp.permission.service.PermissionAbacService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ABAC权限控制器
 */
@RestController
@RequestMapping("/api/permission/abac")
@RequiredArgsConstructor
public class PermissionAbacController {

    private final PermissionAbacService abacService;

    /**
     * 创建ABAC属性
     */
    @PostMapping("/attribute")
    public Result<Long> createAttribute(@RequestBody PermissionAbacAttribute attribute) {
        return Result.success(abacService.createAttribute(attribute));
    }

    /**
     * 更新ABAC属性
     */
    @PutMapping("/attribute")
    public Result<Boolean> updateAttribute(@RequestBody PermissionAbacAttribute attribute) {
        return Result.success(abacService.updateAttribute(attribute));
    }

    /**
     * 删除ABAC属性
     */
    @DeleteMapping("/attribute/{id}")
    public Result<Boolean> deleteAttribute(@PathVariable Long id) {
        return Result.success(abacService.deleteAttribute(id));
    }

    /**
     * 获取所有属性
     */
    @GetMapping("/attributes")
    public Result<List<PermissionAbacAttribute>> getAllAttributes() {
        return Result.success(abacService.getAllAttributes());
    }

    /**
     * 获取用户属性
     */
    @GetMapping("/user-attributes/{userId}")
    public Result<Map<String, Object>> getUserAttributes(@PathVariable Long userId) {
        return Result.success(abacService.getUserAttributes(userId));
    }

    /**
     * 创建ABAC策略
     */
    @PostMapping("/policy")
    public Result<Long> createPolicy(@RequestBody PermissionAbacPolicy policy) {
        return Result.success(abacService.createPolicy(policy));
    }

    /**
     * 更新ABAC策略
     */
    @PutMapping("/policy")
    public Result<Boolean> updatePolicy(@RequestBody PermissionAbacPolicy policy) {
        return Result.success(abacService.updatePolicy(policy));
    }

    /**
     * 删除ABAC策略
     */
    @DeleteMapping("/policy/{id}")
    public Result<Boolean> deletePolicy(@PathVariable Long id) {
        return Result.success(abacService.deletePolicy(id));
    }

    /**
     * 获取策略列表
     */
    @GetMapping("/policies")
    public Result<List<PermissionAbacPolicy>> getPolicies(
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String operationType) {
        if (resourceType != null && operationType != null) {
            return Result.success(abacService.getPoliciesByResourceAndOperation(resourceType, operationType));
        }
        return Result.success(List.of());
    }

    /**
     * 评估ABAC权限
     */
    @PostMapping("/evaluate")
    public Result<Boolean> evaluate(@RequestParam Long userId,
                                     @RequestParam String resourceType,
                                     @RequestParam String operationType,
                                     @RequestBody Map<String, Object> resourceAttributes) {
        return Result.success(abacService.evaluateAbac(userId, resourceType, operationType, resourceAttributes));
    }
}
