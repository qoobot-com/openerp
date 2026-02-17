package com.qoobot.qooerp.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.user.entity.UserInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 创建用户
     */
    Long createUser(UserInfo userInfo);

    /**
     * 更新用户
     */
    boolean updateUser(UserInfo userInfo);

    /**
     * 删除用户（软删除）
     */
    boolean deleteUser(Long userId);

    /**
     * 批量删除用户
     */
    boolean batchDeleteUsers(List<Long> userIds);

    /**
     * 获取用户详情
     */
    UserInfo getUserById(Long userId);

    /**
     * 根据用户名获取用户
     */
    UserInfo getUserByUsername(String username);

    /**
     * 根据手机号获取用户
     */
    UserInfo getUserByPhone(String phone);

    /**
     * 根据邮箱获取用户
     */
    UserInfo getUserByEmail(String email);

    /**
     * 分页查询用户列表
     */
    IPage<UserInfo> pageUsers(Long current, Long size, String username, String phone,
                              String email, String realName, Integer status,
                              LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 搜索用户
     */
    List<UserInfo> searchUsers(String keyword);

    /**
     * 批量获取用户
     */
    List<UserInfo> getUsersByIds(List<Long> userIds);

    /**
     * 启用用户
     */
    boolean enableUser(Long userId);

    /**
     * 禁用用户
     */
    boolean disableUser(Long userId);

    /**
     * 锁定用户
     */
    boolean lockUser(Long userId);

    /**
     * 解锁用户
     */
    boolean unlockUser(Long userId);

    /**
     * 批量启用用户
     */
    boolean batchEnableUsers(List<Long> userIds);

    /**
     * 批量禁用用户
     */
    boolean batchDisableUsers(List<Long> userIds);

    /**
     * 重置密码
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取用户角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 获取用户部门ID列表
     */
    List<Long> getUserDeptIds(Long userId);

    /**
     * 获取用户主部门
     */
    Long getUserPrimaryDept(Long userId);

    /**
     * 获取用户岗位ID列表
     */
    List<Long> getUserPositionIds(Long userId);

    /**
     * 获取用户完整信息（含角色、部门、岗位）
     */
    Map<String, Object> getUserFullInfo(Long userId);

    /**
     * 检查用户名是否存在
     */
    boolean checkUsernameExists(String username);

    /**
     * 检查手机号是否存在
     */
    boolean checkPhoneExists(String phone);

    /**
     * 检查邮箱是否存在
     */
    boolean checkEmailExists(String email);
}
