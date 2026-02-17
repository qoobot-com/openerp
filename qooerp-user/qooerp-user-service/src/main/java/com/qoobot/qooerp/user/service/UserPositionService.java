package com.qoobot.qooerp.user.service;

import java.util.List;

/**
 * 用户岗位服务接口
 */
public interface UserPositionService {

    /**
     * 分配岗位
     */
    boolean assignPositions(Long userId, List<Long> positionIds);

    /**
     * 移除岗位
     */
    boolean removePositions(Long userId, List<Long> positionIds);

    /**
     * 更新用户岗位
     */
    boolean updatePositions(Long userId, List<Long> positionIds);

    /**
     * 清空用户岗位
     */
    boolean clearPositions(Long userId);

    /**
     * 获取用户岗位ID列表
     */
    List<Long> getPositionIds(Long userId);

    /**
     * 批量分配岗位
     */
    boolean batchAssignPositions(List<Long> userIds, List<Long> positionIds);

    /**
     * 批量移除岗位
     */
    boolean batchRemovePositions(List<Long> userIds, List<Long> positionIds);
}
