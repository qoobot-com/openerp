package com.qoobot.qooerp.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper
 */
@Mapper
public interface PermissionRoleMapper extends BaseMapper<PermissionRole> {

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT pr.* FROM permission_role pr " +
            "INNER JOIN permission_user_role pur ON pr.id = pur.role_id " +
            "WHERE pur.user_id = #{userId} AND pr.deleted = 0 AND pr.status = 1")
    List<PermissionRole> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色
     */
    @Select("SELECT * FROM permission_role WHERE role_code = #{roleCode} AND deleted = 0")
    PermissionRole selectByRoleCode(@Param("roleCode") String roleCode);
}
