package com.qoobot.qooerp.permission.service;

import com.qoobot.qooerp.permission.entity.PermissionAbacAttribute;
import com.qoobot.qooerp.permission.entity.PermissionAbacPolicy;

import java.util.List;
import java.util.Map;

/**
 * ABAC权限服务接口
 */
public interface PermissionAbacService {

    /**
     * 创建ABAC属性
     *
     * @param attribute 属性实体
     * @return 属性ID
     */
    Long createAttribute(PermissionAbacAttribute attribute);

    /**
     * 更新ABAC属性
     *
     * @param attribute 属性实体
     * @return 是否成功
     */
    Boolean updateAttribute(PermissionAbacAttribute attribute);

    /**
     * 删除ABAC属性
     *
     * @param id 属性ID
     * @return 是否成功
     */
    Boolean deleteAttribute(Long id);

    /**
     * 获取所有属性
     *
     * @return 属性列表
     */
    List<PermissionAbacAttribute> getAllAttributes();

    /**
     * 获取用户属性值
     *
     * @param userId 用户ID
     * @return 属性键值对
     */
    Map<String, Object> getUserAttributes(Long userId);

    /**
     * 创建ABAC策略
     *
     * @param policy 策略实体
     * @return 策略ID
     */
    Long createPolicy(PermissionAbacPolicy policy);

    /**
     * 更新ABAC策略
     *
     * @param policy 策略实体
     * @return 是否成功
     */
    Boolean updatePolicy(PermissionAbacPolicy policy);

    /**
     * 删除ABAC策略
     *
     * @param id 策略ID
     * @return 是否成功
     */
    Boolean deletePolicy(Long id);

    /**
     * 根据资源类型和操作类型获取策略
     *
     * @param resourceType 资源类型
     * @param operationType 操作类型
     * @return 策略列表
     */
    List<PermissionAbacPolicy> getPoliciesByResourceAndOperation(String resourceType, String operationType);

    /**
     * 评估ABAC权限
     *
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @param operationType 操作类型
     * @param resourceAttributes 资源属性
     * @return 是否有权限
     */
    Boolean evaluateAbac(Long userId, String resourceType, String operationType, Map<String, Object> resourceAttributes);

    /**
     * 评估策略表达式
     *
     * @param expression 表达式
     * @param userAttributes 用户属性
     * @param resourceAttributes 资源属性
     * @return 是否满足条件
     */
    Boolean evaluateExpression(String expression, Map<String, Object> userAttributes, Map<String, Object> resourceAttributes);
}
