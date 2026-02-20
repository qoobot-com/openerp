package com.qoobot.qooerp.permission.controller;

import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.permission.entity.PermissionDataRule;
import com.qoobot.qooerp.permission.enums.DataScopeEnum;
import com.qoobot.qooerp.permission.service.PermissionDataScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据权限控制器
 */
@RestController
@RequestMapping("/api/permission/data-scope")
@RequiredArgsConstructor
public class PermissionDataScopeController {

    private final PermissionDataScopeService dataScopeService;

    /**
     * 创建数据权限规则
     */
    @PostMapping("/rule")
    public Result<Long> createDataRule(@RequestBody PermissionDataRule rule) {
        return Result.success(dataScopeService.createDataRule(rule));
    }

    /**
     * 更新数据权限规则
     */
    @PutMapping("/rule")
    public Result<Boolean> updateDataRule(@RequestBody PermissionDataRule rule) {
        return Result.success(dataScopeService.updateDataRule(rule));
    }

    /**
     * 删除数据权限规则
     */
    @DeleteMapping("/rule/{id}")
    public Result<Boolean> deleteDataRule(@PathVariable Long id) {
        return Result.success(dataScopeService.deleteDataRule(id));
    }

    /**
     * 获取角色的数据权限规则
     */
    @GetMapping("/rule/{roleId}/{resourceType}")
    public Result<PermissionDataRule> getDataRule(@PathVariable Long roleId,
                                                      @PathVariable String resourceType) {
        return Result.success(dataScopeService.getDataRuleByRoleAndResource(roleId, resourceType));
    }

    /**
     * 获取用户的数据权限范围
     */
    @GetMapping("/user/{userId}/{resourceType}")
    public Result<DataScopeEnum> getUserDataScope(@PathVariable Long userId,
                                                     @PathVariable String resourceType) {
        return Result.success(dataScopeService.getUserDataScope(userId, resourceType));
    }

    /**
     * 获取用户可访问的部门ID列表
     */
    @GetMapping("/user/{userId}/{resourceType}/depts")
    public Result<List<Long>> getUserAccessibleDeptIds(@PathVariable Long userId,
                                                         @PathVariable String resourceType) {
        return Result.success(dataScopeService.getUserAccessibleDeptIds(userId, resourceType));
    }

    /**
     * 生成数据权限SQL条件
     */
    @GetMapping("/sql/{userId}/{resourceType}")
    public Result<String> buildDataScopeSql(@PathVariable Long userId,
                                             @PathVariable String resourceType,
                                             @RequestParam String column) {
        return Result.success(dataScopeService.buildDataScopeSql(userId, resourceType, column));
    }

    /**
     * 设置自定义部门权限
     */
    @PostMapping("/custom-depts")
    public Result<Boolean> setCustomDeptIds(@RequestParam Long roleId,
                                             @RequestParam String resourceType,
                                             @RequestBody List<Long> deptIds) {
        return Result.success(dataScopeService.setCustomDeptIds(roleId, resourceType, deptIds));
    }
}
