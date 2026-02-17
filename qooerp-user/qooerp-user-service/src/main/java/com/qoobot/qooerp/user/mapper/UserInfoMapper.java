package com.qoobot.qooerp.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.user.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息Mapper
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 分页查询用户列表
     */
    IPage<UserInfo> selectUserPage(Page<UserInfo> page,
                                    @Param("tenantId") Long tenantId,
                                    @Param("username") String username,
                                    @Param("phone") String phone,
                                    @Param("email") String email,
                                    @Param("realName") String realName,
                                    @Param("status") Integer status,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 根据用户名查询用户
     */
    UserInfo selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    UserInfo selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户
     */
    UserInfo selectByEmail(@Param("email") String email);

    /**
     * 查询用户角色列表
     */
    List<Long> selectUserRoles(@Param("userId") Long userId);

    /**
     * 查询用户部门列表
     */
    List<Long> selectUserDepts(@Param("userId") Long userId);

    /**
     * 查询用户岗位列表
     */
    List<Long> selectUserPositions(@Param("userId") Long userId);

    /**
     * 批量查询用户信息
     */
    List<UserInfo> selectUsersByIds(@Param("userIds") List<Long> userIds);
}
