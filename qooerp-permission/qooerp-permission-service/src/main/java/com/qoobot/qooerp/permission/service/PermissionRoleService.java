package com.qoobot.qooerp.permission.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.permission.dto.PermissionRoleDTO;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.vo.PermissionRoleVO;

import java.util.List;

/**
 * 角色Service接口
 */
public interface PermissionRoleService extends IService<PermissionRole> {

    /**
     * 创建角色
     *
     * @param dto 角色DTO
     * @return 角色ID
     */
    Long createRole(PermissionRoleDTO dto);

    /**
     * 更新角色
     *
     * @param dto 角色DTO
     * @return 是否成功
     */
    Boolean updateRole(PermissionRoleDTO dto);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    Boolean deleteRole(Long id);

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色VO
     */
    PermissionRoleVO getRoleById(Long id);

    /**
     * 分页查询角色
     *
     * @param current 当前页
     * @param size 每页大小
     * @param roleName 角色名称（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<PermissionRoleVO> pageRoles(Long current, Long size, String roleName, Integer status);

    /**
     * 获取所有角色
     *
     * @return 角色列表
     */
    List<PermissionRoleVO> getAllRoles();

    /**
     * 分配菜单给角色
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    Boolean assignMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * 获取角色的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<PermissionRole> getRolesByUserId(Long userId);

    /**
     * 清除角色缓存
     */
    void clearCache();
}
