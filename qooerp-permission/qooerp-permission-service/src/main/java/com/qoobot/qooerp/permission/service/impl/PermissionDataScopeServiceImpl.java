package com.qoobot.qooerp.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.permission.entity.PermissionDataRule;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.enums.DataScopeEnum;
import com.qoobot.qooerp.permission.mapper.PermissionDataRuleMapper;
import com.qoobot.qooerp.permission.service.PermissionDataScopeService;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据权限服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionDataScopeServiceImpl implements PermissionDataScopeService {

    private final PermissionDataRuleMapper dataRuleMapper;
    private final PermissionRoleService roleService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DATA_SCOPE_CACHE_KEY = "permission:data:scope:";
    private static final String DEPT_IDS_CACHE_KEY = "permission:dept:ids:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDataRule(PermissionDataRule rule) {
        dataRuleMapper.insert(rule);
        clearDataRuleCache(rule.getRoleId(), rule.getResourceType());
        return rule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDataRule(PermissionDataRule rule) {
        Boolean result = dataRuleMapper.updateById(rule) > 0;
        clearDataRuleCache(rule.getRoleId(), rule.getResourceType());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDataRule(Long id) {
        PermissionDataRule rule = dataRuleMapper.selectById(id);
        if (rule != null) {
            Boolean result = dataRuleMapper.deleteById(id) > 0;
            clearDataRuleCache(rule.getRoleId(), rule.getResourceType());
            return result;
        }
        return false;
    }

    @Override
    public PermissionDataRule getDataRuleByRoleAndResource(Long roleId, String resourceType) {
        String cacheKey = DATA_SCOPE_CACHE_KEY + roleId + ":" + resourceType;
        PermissionDataRule cached = (PermissionDataRule) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        PermissionDataRule rule = dataRuleMapper.selectOne(new LambdaQueryWrapper<PermissionDataRule>()
                .eq(PermissionDataRule::getRoleId, roleId)
                .eq(PermissionDataRule::getResourceType, resourceType)
                .orderByDesc(PermissionDataRule::getPriority)
                .last("LIMIT 1"));

        if (rule != null) {
            redisTemplate.opsForValue().set(cacheKey, rule, 1, TimeUnit.HOURS);
        }

        return rule;
    }

    @Override
    public DataScopeEnum getUserDataScope(Long userId, String resourceType) {
        List<PermissionRole> roles = roleService.getRolesByUserId(userId);
        if (roles.isEmpty()) {
            return DataScopeEnum.SELF; // 默认只能查看本人数据
        }

        // 获取角色的数据权限范围，优先级最高的生效
        DataScopeEnum result = DataScopeEnum.SELF;
        for (PermissionRole role : roles) {
            PermissionDataRule rule = getDataRuleByRoleAndResource(role.getId(), resourceType);
            if (rule != null && rule.getDataScope() != null) {
                if (result == DataScopeEnum.SELF || rule.getDataScope() < result.getCode()) {
                    result = DataScopeEnum.getByCode(rule.getDataScope());
                }
            }
        }

        return result;
    }

    @Override
    public List<Long> getUserAccessibleDeptIds(Long userId, String resourceType) {
        String cacheKey = DEPT_IDS_CACHE_KEY + userId + ":" + resourceType;
        List<Long> cached = (List<Long>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        DataScopeEnum dataScope = getUserDataScope(userId, resourceType);
        List<Long> deptIds = switch (dataScope) {
            case ALL -> null; // 全部数据，返回null不过滤
            case DEPT -> getUserDeptIds(userId); // 仅本部门
            case DEPT_AND_CHILD -> getUserDeptAndChildIds(userId); // 本部门及子部门
            case SELF -> List.of(); // 仅本人，返回空列表
            case CUSTOM -> getUserCustomDeptIds(userId, resourceType); // 自定义部门
        };

        if (deptIds != null && !deptIds.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, deptIds, 1, TimeUnit.HOURS);
        }

        return deptIds;
    }

    @Override
    public String buildDataScopeSql(Long userId, String resourceType, String column) {
        List<Long> deptIds = getUserAccessibleDeptIds(userId, resourceType);

        if (deptIds == null) {
            return null; // 全部数据，不需要过滤
        }

        if (deptIds.isEmpty()) {
            return column + " = " + userId; // 仅本人数据
        }

        return column + " IN (" + String.join(",",
                deptIds.stream().map(String::valueOf).collect(Collectors.toList())) + ")";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setCustomDeptIds(Long roleId, String resourceType, List<Long> deptIds) {
        PermissionDataRule rule = getDataRuleByRoleAndResource(roleId, resourceType);
        if (rule == null) {
            rule = new PermissionDataRule();
            rule.setRoleId(roleId);
            rule.setResourceType(resourceType);
            rule.setDataScope(DataScopeEnum.CUSTOM.getCode());
            rule.setDeptIds(String.join(",", deptIds.stream().map(String::valueOf).toList()));
            dataRuleMapper.insert(rule);
        } else {
            rule.setDataScope(DataScopeEnum.CUSTOM.getCode());
            rule.setDeptIds(String.join(",", deptIds.stream().map(String::valueOf).toList()));
            dataRuleMapper.updateById(rule);
        }

        clearDataRuleCache(roleId, resourceType);
        return true;
    }

    private List<Long> getUserDeptIds(Long userId) {
        // 这里应该从用户服务获取用户部门ID
        // 简化处理，返回示例数据
        return List.of(1L);
    }

    private List<Long> getUserDeptAndChildIds(Long userId) {
        // 这里应该从组织服务获取用户部门及其子部门ID
        // 简化处理，返回示例数据
        return List.of(1L, 2L, 3L);
    }

    private List<Long> getUserCustomDeptIds(Long userId, String resourceType) {
        List<PermissionRole> roles = roleService.getRolesByUserId(userId);
        Set<Long> deptIds = new HashSet<>();

        for (PermissionRole role : roles) {
            PermissionDataRule rule = getDataRuleByRoleAndResource(role.getId(), resourceType);
            if (rule != null && rule.getDataScope() == DataScopeEnum.CUSTOM.getCode()
                    && StringUtils.hasText(rule.getDeptIds())) {
                String[] ids = rule.getDeptIds().split(",");
                for (String id : ids) {
                    try {
                        deptIds.add(Long.valueOf(id.trim()));
                    } catch (NumberFormatException e) {
                        log.warn("解析部门ID失败: {}", id);
                    }
                }
            }
        }

        return new ArrayList<>(deptIds);
    }

    private void clearDataRuleCache(Long roleId, String resourceType) {
        redisTemplate.delete(DATA_SCOPE_CACHE_KEY + roleId + ":" + resourceType);
    }

    public void clearUserDataScopeCache(Long userId, String resourceType) {
        redisTemplate.delete(DEPT_IDS_CACHE_KEY + userId + ":" + resourceType);
    }
}
