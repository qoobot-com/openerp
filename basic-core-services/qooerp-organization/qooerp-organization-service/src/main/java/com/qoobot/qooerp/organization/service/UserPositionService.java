package com.qoobot.qooerp.organization.service;

import com.qoobot.qooerp.organization.dto.UserPositionDTO;

import java.util.List;
import java.util.Map;

/**
 * 用户岗位关联服务接口
 */
public interface UserPositionService {

    /**
     * 绑定用户岗位
     */
    void bind(UserPositionDTO dto);

    /**
     * 解绑用户岗位
     */
    void unbind(Long userId, Long positionId);

    /**
     * 获取用户岗位列表
     */
    List<Map<String, Object>> getUserPositions(Long userId);

    /**
     * 获取用户主岗位
     */
    Long getUserPrimaryPosition(Long userId);
}
