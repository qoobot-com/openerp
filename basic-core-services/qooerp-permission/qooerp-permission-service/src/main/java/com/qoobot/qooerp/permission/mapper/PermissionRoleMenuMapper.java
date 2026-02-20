package com.qoobot.qooerp.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.permission.entity.PermissionRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

/**
 * 角色菜单关联Mapper
 */
@Mapper
public interface PermissionRoleMenuMapper extends BaseMapper<PermissionRoleMenu> {

    /**
     * 删除角色的所有菜单关联
     *
     * @param roleId 角色ID
     * @return 删除数量
     */
    @Delete("DELETE FROM permission_role_menu WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除菜单的所有角色关联
     *
     * @param menuId 菜单ID
     * @return 删除数量
     */
    @Delete("DELETE FROM permission_role_menu WHERE menu_id = #{menuId}")
    int deleteByMenuId(@Param("menuId") Long menuId);
}
