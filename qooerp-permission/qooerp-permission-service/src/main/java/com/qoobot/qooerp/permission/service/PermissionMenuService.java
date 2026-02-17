package com.qoobot.qooerp.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.permission.dto.PermissionMenuDTO;
import com.qoobot.qooerp.permission.entity.PermissionMenu;
import com.qoobot.qooerp.permission.vo.PermissionMenuVO;
import com.qoobot.qooerp.permission.vo.PermissionTreeVO;

import java.util.List;

/**
 * 菜单Service接口
 */
public interface PermissionMenuService extends IService<PermissionMenu> {

    /**
     * 创建菜单
     *
     * @param dto 菜单DTO
     * @return 菜单ID
     */
    Long createMenu(PermissionMenuDTO dto);

    /**
     * 更新菜单
     *
     * @param dto 菜单DTO
     * @return 是否成功
     */
    Boolean updateMenu(PermissionMenuDTO dto);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否成功
     */
    Boolean deleteMenu(Long id);

    /**
     * 根据ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单VO
     */
    PermissionMenuVO getMenuById(Long id);

    /**
     * 获取所有菜单
     *
     * @return 菜单列表
     */
    List<PermissionMenuVO> getAllMenus();

    /**
     * 获取菜单树
     *
     * @return 菜单树
     */
    List<PermissionMenuVO> getMenuTree();

    /**
     * 获取菜单树（用于角色授权）
     *
     * @param roleId 角色ID
     * @return 菜单树
     */
    List<PermissionTreeVO> getMenuTreeForRole(Long roleId);

    /**
     * 根据用户ID获取菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<PermissionMenuVO> getMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID获取菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<PermissionMenu> getMenusByRoleId(Long roleId);

    /**
     * 清除菜单缓存
     */
    void clearCache();
}
