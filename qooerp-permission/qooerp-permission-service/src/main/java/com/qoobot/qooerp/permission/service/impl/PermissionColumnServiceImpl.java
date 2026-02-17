package com.qoobot.qooerp.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.permission.entity.PermissionColumnRule;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.mapper.PermissionColumnRuleMapper;
import com.qoobot.qooerp.permission.service.PermissionColumnService;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 列级权限服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionColumnServiceImpl extends ServiceImpl<PermissionColumnRuleMapper, PermissionColumnRule>
        implements PermissionColumnService {

    private final PermissionRoleService roleService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COLUMN_PERMISSION_CACHE_KEY = "permission:column:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createColumnRule(PermissionColumnRule rule) {
        save(rule);
        clearColumnRuleCache(rule.getRoleId(), rule.getTableName());
        return rule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateColumnRule(PermissionColumnRule rule) {
        Boolean result = updateById(rule);
        clearColumnRuleCache(rule.getRoleId(), rule.getTableName());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteColumnRule(Long id) {
        PermissionColumnRule rule = getById(id);
        if (rule != null) {
            Boolean result = removeById(id);
            clearColumnRuleCache(rule.getRoleId(), rule.getTableName());
            return result;
        }
        return false;
    }

    @Override
    public List<PermissionColumnRule> getColumnRulesByRoleAndTable(Long roleId, String tableName) {
        String cacheKey = COLUMN_PERMISSION_CACHE_KEY + roleId + ":" + tableName;
        List<PermissionColumnRule> cached = (List<PermissionColumnRule>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<PermissionColumnRule> rules = list(new LambdaQueryWrapper<PermissionColumnRule>()
                .eq(PermissionColumnRule::getRoleId, roleId)
                .eq(PermissionColumnRule::getTableName, tableName));

        redisTemplate.opsForValue().set(cacheKey, rules, 1, TimeUnit.HOURS);
        return rules;
    }

    @Override
    public List<String> getUserAccessibleColumns(Long userId, String tableName) {
        List<PermissionRole> roles = roleService.getRolesByUserId(userId);
        if (roles.isEmpty()) {
            return List.of(); // 无角色，返回空列表
        }

        Set<String> accessibleColumns = new HashSet<>();
        Set<String> hiddenColumns = new HashSet<>();

        for (PermissionRole role : roles) {
            List<PermissionColumnRule> rules = getColumnRulesByRoleAndTable(role.getId(), tableName);
            for (PermissionColumnRule rule : rules) {
                switch (rule.getPermissionType()) {
                    case "VISIBLE" -> accessibleColumns.add(rule.getColumnName());
                    case "HIDDEN" -> hiddenColumns.add(rule.getColumnName());
                    case "MASK" -> accessibleColumns.add(rule.getColumnName()); // 脱敏列也可见
                }
            }
        }

        // 移除隐藏的列
        accessibleColumns.removeAll(hiddenColumns);

        return new ArrayList<>(accessibleColumns);
    }

    @Override
    public Boolean isColumnAccessible(Long userId, String tableName, String columnName) {
        List<String> accessibleColumns = getUserAccessibleColumns(userId, tableName);
        return accessibleColumns.contains(columnName);
    }

    @Override
    public String getColumnMaskType(Long userId, String tableName, String columnName) {
        if (!isColumnAccessible(userId, tableName, columnName)) {
            return null;
        }

        List<PermissionRole> roles = roleService.getRolesByUserId(userId);
        for (PermissionRole role : roles) {
            List<PermissionColumnRule> rules = getColumnRulesByRoleAndTable(role.getId(), tableName);
            for (PermissionColumnRule rule : rules) {
                if (columnName.equals(rule.getColumnName()) && "MASK".equals(rule.getPermissionType())) {
                    return rule.getMaskType();
                }
            }
        }

        return null;
    }

    private void clearColumnRuleCache(Long roleId, String tableName) {
        redisTemplate.delete(COLUMN_PERMISSION_CACHE_KEY + roleId + ":" + tableName);
    }

    public void clearUserColumnCache(Long userId, String tableName) {
        // 清除用户所有角色的列权限缓存
        List<PermissionRole> roles = roleService.getRolesByUserId(userId);
        for (PermissionRole role : roles) {
            redisTemplate.delete(COLUMN_PERMISSION_CACHE_KEY + role.getId() + ":" + tableName);
        }
    }
}
