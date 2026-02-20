package com.qoobot.qooerp.permission.service;

import com.qoobot.qooerp.permission.entity.PermissionColumnRule;

import java.util.List;

/**
 * 列级权限服务接口
 */
public interface PermissionColumnService {

    /**
     * 创建列级权限规则
     *
     * @param rule 列级权限规则
     * @return 规则ID
     */
    Long createColumnRule(PermissionColumnRule rule);

    /**
     * 更新列级权限规则
     *
     * @param rule 列级权限规则
     * @return 是否成功
     */
    Boolean updateColumnRule(PermissionColumnRule rule);

    /**
     * 删除列级权限规则
     *
     * @param id 规则ID
     * @return 是否成功
     */
    Boolean deleteColumnRule(Long id);

    /**
     * 获取角色的列级权限规则
     *
     * @param roleId 角色ID
     * @param tableName 表名
     * @return 列级权限规则列表
     */
    List<PermissionColumnRule> getColumnRulesByRoleAndTable(Long roleId, String tableName);

    /**
     * 获取用户对某表的可访问列
     *
     * @param userId 用户ID
     * @param tableName 表名
     * @return 可访问的列名列表
     */
    List<String> getUserAccessibleColumns(Long userId, String tableName);

    /**
     * 检查列是否可访问
     *
     * @param userId 用户ID
     * @param tableName 表名
     * @param columnName 列名
     * @return 是否可访问
     */
    Boolean isColumnAccessible(Long userId, String tableName, String columnName);

    /**
     * 获取列的脱敏类型
     *
     * @param userId 用户ID
     * @param tableName 表名
     * @param columnName 列名
     * @return 脱敏类型
     */
    String getColumnMaskType(Long userId, String tableName, String columnName);
}
