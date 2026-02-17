package com.qoobot.qooerp.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.permission.entity.PermissionAbacAttribute;
import com.qoobot.qooerp.permission.entity.PermissionAbacPolicy;
import com.qoobot.qooerp.permission.mapper.PermissionAbacAttributeMapper;
import com.qoobot.qooerp.permission.mapper.PermissionAbacPolicyMapper;
import com.qoobot.qooerp.permission.service.PermissionAbacService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ABAC权限服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionAbacServiceImpl extends ServiceImpl<PermissionAbacPolicyMapper, PermissionAbacPolicy>
        implements PermissionAbacService {

    private final PermissionAbacAttributeMapper attributeMapper;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAttribute(PermissionAbacAttribute attribute) {
        attributeMapper.insert(attribute);
        return attribute.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAttribute(PermissionAbacAttribute attribute) {
        return attributeMapper.updateById(attribute) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAttribute(Long id) {
        return attributeMapper.deleteById(id) > 0;
    }

    @Override
    public List<PermissionAbacAttribute> getAllAttributes() {
        return attributeMapper.selectList(null);
    }

    @Override
    public Map<String, Object> getUserAttributes(Long userId) {
        List<PermissionAbacAttribute> attributes = getAllAttributes();
        Map<String, Object> userAttributes = new HashMap<>();

        // 这里应该根据userId查询用户实际属性值
        // 简化处理，返回示例数据
        for (PermissionAbacAttribute attr : attributes) {
            userAttributes.put(attr.getAttributeKey(), getAttributeValue(userId, attr));
        }

        return userAttributes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPolicy(PermissionAbacPolicy policy) {
        baseMapper.insert(policy);
        return policy.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePolicy(PermissionAbacPolicy policy) {
        return baseMapper.updateById(policy) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePolicy(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public List<PermissionAbacPolicy> getPoliciesByResourceAndOperation(String resourceType, String operationType) {
        return baseMapper.selectList(new LambdaQueryWrapper<PermissionAbacPolicy>()
                .eq(PermissionAbacPolicy::getResourceType, resourceType)
                .eq(PermissionAbacPolicy::getOperationType, operationType)
                .eq(PermissionAbacPolicy::getStatus, 1)
                .orderByDesc(PermissionAbacPolicy::getPriority));
    }

    @Override
    public Boolean evaluateAbac(Long userId, String resourceType, String operationType,
                                Map<String, Object> resourceAttributes) {
        List<PermissionAbacPolicy> policies = getPoliciesByResourceAndOperation(resourceType, operationType);

        if (policies.isEmpty()) {
            return false;
        }

        Map<String, Object> userAttributes = getUserAttributes(userId);

        // 按优先级评估策略，任一策略通过即返回true
        for (PermissionAbacPolicy policy : policies) {
            if (evaluateExpression(policy.getPolicyExpression(), userAttributes, resourceAttributes)) {
                log.info("ABAC权限评估通过: userId={}, policy={}", userId, policy.getPolicyName());
                return true;
            }
        }

        log.info("ABAC权限评估未通过: userId={}, resourceType={}, operationType={}",
                userId, resourceType, operationType);
        return false;
    }

    @Override
    public Boolean evaluateExpression(String expression, Map<String, Object> userAttributes,
                                      Map<String, Object> resourceAttributes) {
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();

            // 设置用户属性变量
            userAttributes.forEach((key, value) -> context.setVariable(key, value));

            // 设置资源属性变量
            resourceAttributes.forEach((key, value) -> context.setVariable(key, value));

            // 设置特殊变量
            context.setVariable("user", userAttributes);
            context.setVariable("resource", resourceAttributes);

            // 解析并计算表达式
            Expression expr = parser.parseExpression(expression);
            return expr.getValue(context, Boolean.class);
        } catch (Exception e) {
            log.error("ABAC表达式评估失败: expression={}", expression, e);
            return false;
        }
    }

    private Object getAttributeValue(Long userId, PermissionAbacAttribute attribute) {
        // 根据属性类型和值来源获取实际值
        // 这里简化处理，返回默认值
        return switch (attribute.getAttributeType()) {
            case "USER" -> getUserAttribute(userId, attribute.getAttributeKey());
            case "ENV" -> getEnvironmentAttribute(attribute.getAttributeKey());
            default -> "";
        };
    }

    private Object getUserAttribute(Long userId, String attributeKey) {
        // 这里应该从用户服务或数据库查询
        return switch (attributeKey) {
            case "deptId" -> 1L;
            case "positionId" -> 1L;
            case "level" -> 1;
            case "status" -> 1;
            default -> "";
        };
    }

    private Object getEnvironmentAttribute(String attributeKey) {
        // 获取环境属性
        return switch (attributeKey) {
            case "currentTime" -> System.currentTimeMillis();
            case "currentHour" -> java.time.LocalDateTime.now().getHour();
            default -> "";
        };
    }
}
