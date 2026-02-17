package com.qoobot.qooerp.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.DataNotFoundException;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.organization.dto.UserPositionDTO;
import com.qoobot.qooerp.organization.entity.OrganizationUserPosition;
import com.qoobot.qooerp.organization.mapper.OrganizationUserPositionMapper;
import com.qoobot.qooerp.organization.service.UserPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户岗位关联服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPositionServiceImpl extends ServiceImpl<OrganizationUserPositionMapper, OrganizationUserPosition>
        implements UserPositionService {

    private final OrganizationUserPositionMapper userPositionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bind(UserPositionDTO dto) {
        // 取消该用户的所有主岗位标记
        LambdaQueryWrapper<OrganizationUserPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationUserPosition::getUserId, dto.getUserId());
        wrapper.eq(OrganizationUserPosition::getIsPrimary, 1);
        List<OrganizationUserPosition> existingPrimary = userPositionMapper.selectList(wrapper);
        for (OrganizationUserPosition item : existingPrimary) {
            item.setIsPrimary(0);
            userPositionMapper.updateById(item);
        }

        // 检查是否已绑定
        LambdaQueryWrapper<OrganizationUserPosition> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(OrganizationUserPosition::getUserId, dto.getUserId());
        checkWrapper.eq(OrganizationUserPosition::getPositionId, dto.getPositionId());
        checkWrapper.eq(OrganizationUserPosition::getStatus, 1);
        OrganizationUserPosition existing = userPositionMapper.selectOne(checkWrapper);

        if (existing != null) {
            // 更新主岗位标记
            if (dto.getIsPrimary() != null && dto.getIsPrimary() == 1) {
                existing.setIsPrimary(1);
                userPositionMapper.updateById(existing);
            }
        } else {
            // 新增绑定
            OrganizationUserPosition userPosition = new OrganizationUserPosition();
            userPosition.setUserId(dto.getUserId());
            userPosition.setPositionId(dto.getPositionId());
            userPosition.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : 0);
            userPosition.setAppointTime(dto.getAppointTime() != null ? dto.getAppointTime() : LocalDateTime.now());
            userPosition.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            userPosition.setRemark(dto.getRemark());
            userPosition.setTenantId(TenantContextHolder.getTenantId());
            userPosition.setCreateTime(LocalDateTime.now());
            userPositionMapper.insert(userPosition);
        }

        log.info("绑定用户岗位成功: userId={}, positionId={}", dto.getUserId(), dto.getPositionId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbind(Long userId, Long positionId) {
        LambdaQueryWrapper<OrganizationUserPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationUserPosition::getUserId, userId);
        wrapper.eq(OrganizationUserPosition::getPositionId, positionId);

        OrganizationUserPosition userPosition = userPositionMapper.selectOne(wrapper);
        if (userPosition == null) {
            throw new DataNotFoundException("用户岗位关联不存在");
        }

        userPosition.setStatus(0);
        userPosition.setDismissTime(LocalDateTime.now());
        userPositionMapper.updateById(userPosition);

        log.info("解绑用户岗位成功: userId={}, positionId={}", userId, positionId);
    }

    @Override
    public List<Map<String, Object>> getUserPositions(Long userId) {
        LambdaQueryWrapper<OrganizationUserPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationUserPosition::getUserId, userId);
        wrapper.eq(OrganizationUserPosition::getStatus, 1);
        wrapper.orderByDesc(OrganizationUserPosition::getIsPrimary);
        wrapper.orderByDesc(OrganizationUserPosition::getAppointTime);

        return userPositionMapper.selectList(wrapper).stream()
                .map(userPos -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", userPos.getId());
                    map.put("userId", userPos.getUserId());
                    map.put("positionId", userPos.getPositionId());
                    map.put("isPrimary", userPos.getIsPrimary());
                    map.put("appointTime", userPos.getAppointTime());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long getUserPrimaryPosition(Long userId) {
        LambdaQueryWrapper<OrganizationUserPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationUserPosition::getUserId, userId);
        wrapper.eq(OrganizationUserPosition::getIsPrimary, 1);
        wrapper.eq(OrganizationUserPosition::getStatus, 1);
        wrapper.last("LIMIT 1");

        OrganizationUserPosition userPosition = userPositionMapper.selectOne(wrapper);
        return userPosition != null ? userPosition.getPositionId() : null;
    }
}
