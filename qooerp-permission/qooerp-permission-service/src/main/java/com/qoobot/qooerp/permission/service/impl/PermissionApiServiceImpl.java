package com.qoobot.qooerp.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.permission.entity.PermissionApi;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.mapper.PermissionApiMapper;
import com.qoobot.qooerp.permission.mapper.PermissionRoleMapper;
import com.qoobot.qooerp.permission.service.PermissionApiService;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 接口权限服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionApiServiceImpl extends ServiceImpl<PermissionApiMapper, PermissionApi> implements PermissionApiService {

    private final PermissionRoleMapper roleMapper;
    private final PermissionRoleService roleService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String API_PERMISSION_CACHE_KEY = "permission:api:user:";
    private static final String ROLE_API_CACHE_KEY = "permission:api:role:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createApi(PermissionApi api) {
        return save(api);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateApi(PermissionApi api) {
        return updateById(api);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteApi(Long id) {
        return removeById(id);
    }

    @Override
    public Boolean checkApiPermission(Long userId, String apiPath, String httpMethod) {
        Set<String> permissions = getUserApiPermissions(userId);
        return permissions.stream()
                .anyMatch(p -> {
                    PermissionApi api = getApiByPermission(p);
                    if (api == null) {
                        return false;
                    }
                    return apiPath.equals(api.getApiPath()) && httpMethod.equalsIgnoreCase(api.getHttpMethod());
                });
    }

    @Override
    public Set<String> getUserApiPermissions(Long userId) {
        String cacheKey = API_PERMISSION_CACHE_KEY + userId;
        Set<String> cached = (Set<String>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<PermissionRole> roles = roleService.getRolesByUserId(userId);
        if (roles.isEmpty()) {
            return Set.of();
        }

        Set<String> permissions = roles.stream()
                .flatMap(role -> getApiPermissionsByRoleId(role.getId()).stream())
                .collect(Collectors.toSet());

        redisTemplate.opsForValue().set(cacheKey, permissions, 1, TimeUnit.HOURS);
        return permissions;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean assignApisToRole(Long roleId, List<Long> apiIds) {
        // 清除缓存
        clearRoleApiCache(roleId);

        // 这里需要实现角色与API的关联表，简化处理直接返回true
        // 实际应该创建 permission_role_api 关联表
        log.info("为角色分配API权限: roleId={}, apiCount={}", roleId, apiIds.size());
        return true;
    }

    @Override
    public List<Long> getApiIdsByRoleId(Long roleId) {
        String cacheKey = ROLE_API_CACHE_KEY + roleId;
        List<Long> cached = (List<Long>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 这里需要从关联表查询，简化处理返回空列表
        List<Long> apiIds = List.of();

        redisTemplate.opsForValue().set(cacheKey, apiIds, 1, TimeUnit.HOURS);
        return apiIds;
    }

    private List<String> getApiPermissionsByRoleId(Long roleId) {
        return getApiIdsByRoleId(roleId).stream()
                .map(this::getApiById)
                .map(PermissionApi::getPermission)
                .collect(Collectors.toList());
    }

    private PermissionApi getApiById(Long id) {
        return getById(id);
    }

    private PermissionApi getApiByPermission(String permission) {
        return getOne(new LambdaQueryWrapper<PermissionApi>()
                .eq(PermissionApi::getPermission, permission));
    }

    private void clearRoleApiCache(Long roleId) {
        redisTemplate.delete(ROLE_API_CACHE_KEY + roleId);
    }

    public void clearUserApiCache(Long userId) {
        redisTemplate.delete(API_PERMISSION_CACHE_KEY + userId);
    }
}
