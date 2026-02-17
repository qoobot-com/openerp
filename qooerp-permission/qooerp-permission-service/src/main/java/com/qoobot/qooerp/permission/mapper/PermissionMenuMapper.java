package com.qoobot.qooerp.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.permission.entity.PermissionMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper
 */
@Mapper
public interface PermissionMenuMapper extends BaseMapper<PermissionMenu> {

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    @Select("SELECT pm.* FROM permission_menu pm " +
            "INNER JOIN permission_role_menu prm ON pm.id = prm.menu_id " +
            "WHERE prm.role_id = #{roleId} " +
            "ORDER BY pm.sort ASC")
    List<PermissionMenu> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Select("SELECT DISTINCT pm.* FROM permission_menu pm " +
            "INNER JOIN permission_role_menu prm ON pm.id = prm.menu_id " +
            "INNER JOIN permission_user_role pur ON prm.role_id = pur.role_id " +
            "WHERE pur.user_id = #{userId} AND pm.visible = 1 " +
            "ORDER BY pm.sort ASC")
    List<PermissionMenu> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询所有菜单（按父ID和排序）
     *
     * @return 菜单列表
     */
    @Select("SELECT * FROM permission_menu ORDER BY parent_id ASC, sort ASC")
    List<PermissionMenu> selectAllSorted();
}
