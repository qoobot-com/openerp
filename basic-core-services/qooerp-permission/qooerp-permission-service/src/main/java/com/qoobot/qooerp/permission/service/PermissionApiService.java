package com.qoobot.qooerp.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.permission.entity.PermissionApi;

import java.util.List;
import java.util.Set;

/**
 * 接口权限服务接口
 */
public interface PermissionApiService extends IService<PermissionApi> {

    /**
     * 创建接口权限
     *
     * @param api 接口权限实体
     * @return 是否成功
     */
    Boolean createApi(PermissionApi api);

    /**
     * 更新接口权限
     *
     * @param api 接口权限实体
     * @return 是否成功
     */
    Boolean updateApi(PermissionApi api);

    /**
     * 删除接口权限
     *
     * @param id 接口ID
     * @return 是否成功
     */
    Boolean deleteApi(Long id);

    /**
     * 验证API访问权限
     *
     * @param userId 用户ID
     * @param apiPath API路径
     * @param httpMethod HTTP方法
     * @return 是否有权限
     */
    Boolean checkApiPermission(Long userId, String apiPath, String httpMethod);

    /**
     * 获取用户所有接口权限标识
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> getUserApiPermissions(Long userId);

    /**
     * 为角色分配接口权限
     *
     * @param roleId 角色ID
     * @param apiIds API ID列表
     * @return 是否成功
     */
    Boolean assignApisToRole(Long roleId, List<Long> apiIds);

    /**
     * 获取角色的API权限ID列表
     *
     * @param roleId 角色ID
     * @return API ID列表
     */
    List<Long> getApiIdsByRoleId(Long roleId);
}
