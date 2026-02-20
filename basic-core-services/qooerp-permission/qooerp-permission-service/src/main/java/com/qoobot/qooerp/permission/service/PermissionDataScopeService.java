package com.qoobot.qooerp.permission.service;

import com.qoobot.qooerp.permission.entity.PermissionDataRule;
import com.qoobot.qooerp.permission.enums.DataScopeEnum;

import java.util.List;

/**
 * 数据权限服务接口
 */
public interface PermissionDataScopeService {

    /**
     * 创建数据权限规则
     *
     * @param rule 数据权限规则
     * @return 规则ID
     */
    Long createDataRule(PermissionDataRule rule);

    /**
     * 更新数据权限规则
     *
     * @param rule 数据权限规则
     * @return 是否成功
     */
    Boolean updateDataRule(PermissionDataRule rule);

    /**
     * 删除数据权限规则
     *
     * @param id 规则ID
     * @return 是否成功
     */
    Boolean deleteDataRule(Long id);

    /**
     * 获取角色的数据权限规则
     *
     * @param roleId 角色ID
     * @param resourceType 资源类型
     * @return 数据权限规则
     */
    PermissionDataRule getDataRuleByRoleAndResource(Long roleId, String resourceType);

    /**
     * 获取用户的数据权限范围
     *
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @return 数据权限范围
     */
    DataScopeEnum getUserDataScope(Long userId, String resourceType);

    /**
     * 获取用户可访问的部门ID列表
     *
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @return 部门ID列表
     */
    List<Long> getUserAccessibleDeptIds(Long userId, String resourceType);

    /**
     * 生成数据权限SQL条件
     *
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @param column 数据列名（如dept_id, create_by）
     * @return SQL条件
     */
    String buildDataScopeSql(Long userId, String resourceType, String column);

    /**
     * 设置自定义部门权限
     *
     * @param roleId 角色ID
     * @param resourceType 资源类型
     * @param deptIds 部门ID列表
     * @return 是否成功
     */
    Boolean setCustomDeptIds(Long roleId, String resourceType, List<Long> deptIds);
}
