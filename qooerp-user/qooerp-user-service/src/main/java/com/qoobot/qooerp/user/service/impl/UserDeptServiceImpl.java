package com.qoobot.qooerp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.user.entity.UserDept;
import com.qoobot.qooerp.user.mapper.UserDeptMapper;
import com.qoobot.qooerp.user.service.UserDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户部门服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeptServiceImpl implements UserDeptService {

    private final UserDeptMapper userDeptMapper;
    private static final String USER_DEPTS_CACHE_PREFIX = "user:depts:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_DEPTS_CACHE_PREFIX, key = "#userId")
    public boolean assignDepts(Long userId, List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return true;
        }

        // 删除旧的部门关联
        removeDepts(userId, null);

        // 添加新的部门关联
        int rows = 0;
        for (int i = 0; i < deptIds.size(); i++) {
            UserDept userDept = new UserDept();
            userDept.setUserId(userId);
            userDept.setDeptId(deptIds.get(i));
            userDept.setIsPrimary(i == 0 ? 1 : 0); // 第一个设为主部门
            if (userDeptMapper.insert(userDept) > 0) {
                rows++;
            }
        }

        log.info("分配用户部门成功，userId={}, deptIds={}, count={}", userId, deptIds, rows);
        return rows == deptIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setPrimaryDept(Long userId, Long deptId) {
        // 清除所有主部门标记
        LambdaQueryWrapper<UserDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDept::getUserId, userId);
        wrapper.eq(UserDept::getIsPrimary, 1);
        List<UserDept> primaryDepts = userDeptMapper.selectList(wrapper);
        for (UserDept ud : primaryDepts) {
            ud.setIsPrimary(0);
            userDeptMapper.updateById(ud);
        }

        // 设置新的主部门
        LambdaQueryWrapper<UserDept> newWrapper = new LambdaQueryWrapper<>();
        newWrapper.eq(UserDept::getUserId, userId);
        newWrapper.eq(UserDept::getDeptId, deptId);
        UserDept userDept = userDeptMapper.selectOne(newWrapper);
        if (userDept != null) {
            userDept.setIsPrimary(1);
            int rows = userDeptMapper.updateById(userDept);
            log.info("设置用户主部门成功，userId={}, deptId={}", userId, deptId);
            return rows > 0;
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_DEPTS_CACHE_PREFIX, key = "#userId")
    public boolean removeDepts(Long userId, List<Long> deptIds) {
        LambdaQueryWrapper<UserDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDept::getUserId, userId);

        if (deptIds != null && !deptIds.isEmpty()) {
            wrapper.in(UserDept::getDeptId, deptIds);
        }

        int rows = userDeptMapper.delete(wrapper);
        log.info("移除用户部门成功，userId={}, deptIds={}, count={}", userId, deptIds, rows);
        return rows >= 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_DEPTS_CACHE_PREFIX, key = "#userId")
    public boolean updateDepts(Long userId, List<Long> deptIds) {
        return assignDepts(userId, deptIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_DEPTS_CACHE_PREFIX, key = "#userId")
    public boolean clearDepts(Long userId) {
        return removeDepts(userId, null);
    }

    @Override
    @Cacheable(value = USER_DEPTS_CACHE_PREFIX, key = "#userId")
    public List<Long> getDeptIds(Long userId) {
        LambdaQueryWrapper<UserDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDept::getUserId, userId);
        wrapper.select(UserDept::getDeptId);

        return userDeptMapper.selectList(wrapper).stream()
                .map(UserDept::getDeptId)
                .collect(Collectors.toList());
    }

    @Override
    public Long getPrimaryDept(Long userId) {
        LambdaQueryWrapper<UserDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDept::getUserId, userId);
        wrapper.eq(UserDept::getIsPrimary, 1);
        wrapper.select(UserDept::getDeptId);

        return userDeptMapper.selectList(wrapper).stream()
                .map(UserDept::getDeptId)
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAssignDepts(List<Long> userIds, List<Long> deptIds) {
        if (userIds == null || userIds.isEmpty() || deptIds == null || deptIds.isEmpty()) {
            return true;
        }

        int totalRows = 0;
        for (Long userId : userIds) {
            if (assignDepts(userId, deptIds)) {
                totalRows++;
            }
        }
        log.info("批量分配用户部门成功，userIds={}, deptIds={}, count={}", userIds, deptIds, totalRows);
        return totalRows == userIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRemoveDepts(List<Long> userIds, List<Long> deptIds) {
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }

        int totalRows = 0;
        for (Long userId : userIds) {
            if (removeDepts(userId, deptIds)) {
                totalRows++;
            }
        }
        log.info("批量移除用户部门成功，userIds={}, deptIds={}, count={}", userIds, deptIds, totalRows);
        return totalRows == userIds.size();
    }
}
