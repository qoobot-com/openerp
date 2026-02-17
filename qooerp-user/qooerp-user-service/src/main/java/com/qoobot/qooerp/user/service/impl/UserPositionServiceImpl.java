package com.qoobot.qooerp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.user.entity.UserPosition;
import com.qoobot.qooerp.user.mapper.UserPositionMapper;
import com.qoobot.qooerp.user.service.UserPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户岗位服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPositionServiceImpl implements UserPositionService {

    private final UserPositionMapper userPositionMapper;
    private static final String USER_POSITIONS_CACHE_PREFIX = "user:positions:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_POSITIONS_CACHE_PREFIX, key = "#userId")
    public boolean assignPositions(Long userId, List<Long> positionIds) {
        if (positionIds == null || positionIds.isEmpty()) {
            return true;
        }

        // 删除旧的岗位关联
        removePositions(userId, null);

        // 添加新的岗位关联
        int rows = 0;
        for (Long positionId : positionIds) {
            UserPosition userPosition = new UserPosition();
            userPosition.setUserId(userId);
            userPosition.setPositionId(positionId);
            if (userPositionMapper.insert(userPosition) > 0) {
                rows++;
            }
        }

        log.info("分配用户岗位成功，userId={}, positionIds={}, count={}", userId, positionIds, rows);
        return rows == positionIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_POSITIONS_CACHE_PREFIX, key = "#userId")
    public boolean removePositions(Long userId, List<Long> positionIds) {
        LambdaQueryWrapper<UserPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPosition::getUserId, userId);

        if (positionIds != null && !positionIds.isEmpty()) {
            wrapper.in(UserPosition::getPositionId, positionIds);
        }

        int rows = userPositionMapper.delete(wrapper);
        log.info("移除用户岗位成功，userId={}, positionIds={}, count={}", userId, positionIds, rows);
        return rows >= 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_POSITIONS_CACHE_PREFIX, key = "#userId")
    public boolean updatePositions(Long userId, List<Long> positionIds) {
        return assignPositions(userId, positionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_POSITIONS_CACHE_PREFIX, key = "#userId")
    public boolean clearPositions(Long userId) {
        return removePositions(userId, null);
    }

    @Override
    @Cacheable(value = USER_POSITIONS_CACHE_PREFIX, key = "#userId")
    public List<Long> getPositionIds(Long userId) {
        LambdaQueryWrapper<UserPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPosition::getUserId, userId);
        wrapper.select(UserPosition::getPositionId);

        return userPositionMapper.selectList(wrapper).stream()
                .map(UserPosition::getPositionId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAssignPositions(List<Long> userIds, List<Long> positionIds) {
        if (userIds == null || userIds.isEmpty() || positionIds == null || positionIds.isEmpty()) {
            return true;
        }

        int totalRows = 0;
        for (Long userId : userIds) {
            if (assignPositions(userId, positionIds)) {
                totalRows++;
            }
        }
        log.info("批量分配用户岗位成功，userIds={}, positionIds={}, count={}", userIds, positionIds, totalRows);
        return totalRows == userIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRemovePositions(List<Long> userIds, List<Long> positionIds) {
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }

        int totalRows = 0;
        for (Long userId : userIds) {
            if (removePositions(userId, positionIds)) {
                totalRows++;
            }
        }
        log.info("批量移除用户岗位成功，userIds={}, positionIds={}, count={}", userIds, positionIds, totalRows);
        return totalRows == userIds.size();
    }
}
