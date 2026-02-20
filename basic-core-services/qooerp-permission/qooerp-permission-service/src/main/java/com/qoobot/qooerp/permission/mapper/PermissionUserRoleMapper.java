package com.qoobot.qooerp.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.permission.entity.PermissionUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

/**
 * 用户角色关联Mapper
 */
@Mapper
public interface PermissionUserRoleMapper extends BaseMapper<PermissionUserRole> {

    /**
     * 删除用户的所有角色关联
     *
     * @param userId 用户ID
     * @return 删除数量
     */
    @Delete("DELETE FROM permission_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除角色的所有用户关联
     *
     * @param roleId 角色ID
     * @return 删除数量
     */
    @Delete("DELETE FROM permission_user_role WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);
}
