package com.qoobot.qooerp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.user.entity.UserInfo;
import com.qoobot.qooerp.user.mapper.UserInfoMapper;
import com.qoobot.qooerp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserService {

    private final UserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_CACHE_PREFIX = "user:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_CACHE_PREFIX, key = "#result")
    public Long createUser(UserInfo userInfo) {
        // 校验用户名唯一性
        if (checkUsernameExists(userInfo.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        // 校验手机号唯一性
        if (StringUtils.hasText(userInfo.getPhone()) && checkPhoneExists(userInfo.getPhone())) {
            throw new BusinessException("手机号已存在");
        }
        // 校验邮箱唯一性
        if (StringUtils.hasText(userInfo.getEmail()) && checkEmailExists(userInfo.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        // 设置默认状态
        if (userInfo.getStatus() == null) {
            userInfo.setStatus(1); // 默认启用
        }

        // 设置创建人ID
        Long currentUserId = getCurrentUserId();
        userInfo.setCreateBy(currentUserId);
        userInfo.setUpdateBy(currentUserId);

        // 设置租户ID
        if (userInfo.getTenantId() == null) {
            userInfo.setTenantId(SecurityUtils.getTenantId());
        }

        userInfoMapper.insert(userInfo);
        log.info("创建用户成功，userId={}, username={}", userInfo.getId(), userInfo.getUsername());
        return userInfo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_CACHE_PREFIX, key = "#userInfo.id")
    public boolean updateUser(UserInfo userInfo) {
        UserInfo existUser = getUserById(userInfo.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验用户名唯一性（排除自己）
        if (!existUser.getUsername().equals(userInfo.getUsername()) &&
            checkUsernameExists(userInfo.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 校验手机号唯一性
        if (StringUtils.hasText(userInfo.getPhone()) &&
            (!Objects.equals(existUser.getPhone(), userInfo.getPhone())) &&
            checkPhoneExists(userInfo.getPhone())) {
            throw new BusinessException("手机号已存在");
        }

        // 校验邮箱唯一性
        if (StringUtils.hasText(userInfo.getEmail()) &&
            (!Objects.equals(existUser.getEmail(), userInfo.getEmail())) &&
            checkEmailExists(userInfo.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        // 设置更新人ID
        userInfo.setUpdateBy(getCurrentUserId());
        userInfo.setUpdateTime(LocalDateTime.now());

        int rows = userInfoMapper.updateById(userInfo);
        log.info("更新用户成功，userId={}, username={}", userInfo.getId(), userInfo.getUsername());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_CACHE_PREFIX, key = "#userId")
    public boolean deleteUser(Long userId) {
        UserInfo user = getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 软删除
        user.setDeleted(1);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(getCurrentUserId());

        int rows = userInfoMapper.updateById(user);
        log.info("删除用户成功，userId={}, username={}", userId, user.getUsername());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }

        int rows = 0;
        for (Long userId : userIds) {
            if (deleteUser(userId)) {
                rows++;
            }
        }
        log.info("批量删除用户成功，count={}", rows);
        return rows == userIds.size();
    }

    @Override
    @Cacheable(value = USER_CACHE_PREFIX, key = "#userId")
    public UserInfo getUserById(Long userId) {
        return userInfoMapper.selectById(userId);
    }

    @Override
    @Cacheable(value = USER_CACHE_PREFIX + ":username", key = "#username")
    public UserInfo getUserByUsername(String username) {
        return userInfoMapper.selectByUsername(username);
    }

    @Override
    @Cacheable(value = USER_CACHE_PREFIX + ":phone", key = "#phone")
    public UserInfo getUserByPhone(String phone) {
        return userInfoMapper.selectByPhone(phone);
    }

    @Override
    @Cacheable(value = USER_CACHE_PREFIX + ":email", key = "#email")
    public UserInfo getUserByEmail(String email) {
        return userInfoMapper.selectByEmail(email);
    }

    @Override
    public IPage<UserInfo> pageUsers(Long current, Long size, String username, String phone,
                                       String email, String realName, Integer status,
                                       LocalDateTime startTime, LocalDateTime endTime) {
        Page<UserInfo> page = new Page<>(current, size);
        Long tenantId = SecurityUtils.getTenantId();
        return userInfoMapper.selectUserPage(page, tenantId, username, phone,
                email, realName, status, startTime, endTime);
    }

    @Override
    public List<UserInfo> searchUsers(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(UserInfo::getUsername, keyword)
                .or().like(UserInfo::getRealName, keyword)
                .or().like(UserInfo::getNickname, keyword)
                .or().like(UserInfo::getPhone, keyword)
                .or().like(UserInfo::getEmail, keyword))
                .eq(UserInfo::getDeleted, 0)
                .eq(UserInfo::getTenantId, SecurityUtils.getTenantId())
                .last("LIMIT 100");

        return userInfoMapper.selectList(wrapper);
    }

    @Override
    public List<UserInfo> getUsersByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userInfoMapper.selectUsersByIds(userIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_CACHE_PREFIX, key = "#userId")
    public boolean enableUser(Long userId) {
        UserInfo user = getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(1);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(getCurrentUserId());

        int rows = userInfoMapper.updateById(user);
        log.info("启用用户成功，userId={}, username={}", userId, user.getUsername());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_CACHE_PREFIX, key = "#userId")
    public boolean disableUser(Long userId) {
        UserInfo user = getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(0);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(getCurrentUserId());

        int rows = userInfoMapper.updateById(user);
        log.info("禁用用户成功，userId={}, username={}", userId, user.getUsername());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_CACHE_PREFIX, key = "#userId")
    public boolean lockUser(Long userId) {
        UserInfo user = getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(0); // 锁定即禁用
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(getCurrentUserId());

        int rows = userInfoMapper.updateById(user);
        log.info("锁定用户成功，userId={}, username={}", userId, user.getUsername());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_CACHE_PREFIX, key = "#userId")
    public boolean unlockUser(Long userId) {
        UserInfo user = getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(1);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(getCurrentUserId());

        int rows = userInfoMapper.updateById(user);
        log.info("解锁用户成功，userId={}, username={}", userId, user.getUsername());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchEnableUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }

        int rows = 0;
        for (Long userId : userIds) {
            if (enableUser(userId)) {
                rows++;
            }
        }
        log.info("批量启用用户成功，count={}", rows);
        return rows == userIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDisableUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }

        int rows = 0;
        for (Long userId : userIds) {
            if (disableUser(userId)) {
                rows++;
            }
        }
        log.info("批量禁用用户成功，count={}", rows);
        return rows == userIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId, String newPassword) {
        UserInfo user = getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(getCurrentUserId());

        int rows = userInfoMapper.updateById(user);
        log.info("重置密码成功，userId={}, username={}", userId, user.getUsername());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        UserInfo user = getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(getCurrentUserId());

        int rows = userInfoMapper.updateById(user);
        log.info("修改密码成功，userId={}, username={}", userId, user.getUsername());
        return rows > 0;
    }

    @Override
    @Cacheable(value = USER_CACHE_PREFIX + ":roles", key = "#userId")
    public List<Long> getUserRoleIds(Long userId) {
        return userInfoMapper.selectUserRoles(userId);
    }

    @Override
    @Cacheable(value = USER_CACHE_PREFIX + ":depts", key = "#userId")
    public List<Long> getUserDeptIds(Long userId) {
        return userInfoMapper.selectUserDepts(userId);
    }

    @Override
    @Cacheable(value = USER_CACHE_PREFIX + ":primaryDept", key = "#userId")
    public Long getUserPrimaryDept(Long userId) {
        return userInfoMapper.selectUserDepts(userId).stream().findFirst().orElse(null);
    }

    @Override
    @Cacheable(value = USER_CACHE_PREFIX + ":positions", key = "#userId")
    public List<Long> getUserPositionIds(Long userId) {
        return userInfoMapper.selectUserPositions(userId);
    }

    @Override
    public Map<String, Object> getUserFullInfo(Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("user", getUserById(userId));
        result.put("roleIds", getUserRoleIds(userId));
        result.put("deptIds", getUserDeptIds(userId));
        result.put("positionIds", getUserPositionIds(userId));
        return result;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        UserInfo user = getUserByUsername(username);
        return user != null && user.getDeleted() == 0;
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        UserInfo user = getUserByPhone(phone);
        return user != null && user.getDeleted() == 0;
    }

    @Override
    public boolean checkEmailExists(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        UserInfo user = getUserByEmail(email);
        return user != null && user.getDeleted() == 0;
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            return SecurityUtils.getUserIdOrDefault(0L);
        } catch (Exception e) {
            return 0L;
        }
    }
}
