package com.qoobot.qooerp.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.DataNotFoundException;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.organization.dto.UserDeptDTO;
import com.qoobot.qooerp.organization.entity.OrganizationUserDept;
import com.qoobot.qooerp.organization.mapper.OrganizationUserDeptMapper;
import com.qoobot.qooerp.organization.service.UserDeptService;
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
 * 用户部门关联服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeptServiceImpl extends ServiceImpl<OrganizationUserDeptMapper, OrganizationUserDept>
        implements UserDeptService {

    private final OrganizationUserDeptMapper userDeptMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bind(UserDeptDTO dto) {
        // 取消该用户的所有主部门标记
        LambdaQueryWrapper<OrganizationUserDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationUserDept::getUserId, dto.getUserId());
        wrapper.eq(OrganizationUserDept::getIsPrimary, 1);
        List<OrganizationUserDept> existingPrimary = userDeptMapper.selectList(wrapper);
        for (OrganizationUserDept item : existingPrimary) {
            item.setIsPrimary(0);
            userDeptMapper.updateById(item);
        }

        // 检查是否已绑定
        LambdaQueryWrapper<OrganizationUserDept> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(OrganizationUserDept::getUserId, dto.getUserId());
        checkWrapper.eq(OrganizationUserDept::getDeptId, dto.getDeptId());
        checkWrapper.eq(OrganizationUserDept::getStatus, 1);
        OrganizationUserDept existing = userDeptMapper.selectOne(checkWrapper);

        if (existing != null) {
            // 更新主部门标记
            if (dto.getIsPrimary() != null && dto.getIsPrimary() == 1) {
                existing.setIsPrimary(1);
                existing.setUpdateTime(LocalDateTime.now());
                userDeptMapper.updateById(existing);
            }
        } else {
            // 新增绑定
            OrganizationUserDept userDept = new OrganizationUserDept();
            userDept.setUserId(dto.getUserId());
            userDept.setDeptId(dto.getDeptId());
            userDept.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : 0);
            userDept.setJoinTime(dto.getJoinTime() != null ? dto.getJoinTime() : LocalDateTime.now());
            userDept.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            userDept.setRemark(dto.getRemark());
            userDept.setTenantId(TenantContextHolder.getTenantId());
            userDept.setCreateTime(LocalDateTime.now());
            userDeptMapper.insert(userDept);
        }

        log.info("绑定用户部门成功: userId={}, deptId={}", dto.getUserId(), dto.getDeptId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbind(Long userId, Long deptId) {
        LambdaQueryWrapper<OrganizationUserDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationUserDept::getUserId, userId);
        wrapper.eq(OrganizationUserDept::getDeptId, deptId);

        OrganizationUserDept userDept = userDeptMapper.selectOne(wrapper);
        if (userDept == null) {
            throw new DataNotFoundException("用户部门关联不存在");
        }

        userDept.setStatus(0);
        userDept.setLeaveTime(LocalDateTime.now());
        userDeptMapper.updateById(userDept);

        log.info("解绑用户部门成功: userId={}, deptId={}", userId, deptId);
    }

    @Override
    public List<Map<String, Object>> getUserDepts(Long userId) {
        LambdaQueryWrapper<OrganizationUserDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationUserDept::getUserId, userId);
        wrapper.eq(OrganizationUserDept::getStatus, 1);
        wrapper.orderByDesc(OrganizationUserDept::getIsPrimary);
        wrapper.orderByDesc(OrganizationUserDept::getJoinTime);

        return userDeptMapper.selectList(wrapper).stream()
                .map(userDept -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", userDept.getId());
                    map.put("userId", userDept.getUserId());
                    map.put("deptId", userDept.getDeptId());
                    map.put("isPrimary", userDept.getIsPrimary());
                    map.put("joinTime", userDept.getJoinTime());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long getUserPrimaryDept(Long userId) {
        LambdaQueryWrapper<OrganizationUserDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationUserDept::getUserId, userId);
        wrapper.eq(OrganizationUserDept::getIsPrimary, 1);
        wrapper.eq(OrganizationUserDept::getStatus, 1);
        wrapper.last("LIMIT 1");

        OrganizationUserDept userDept = userDeptMapper.selectOne(wrapper);
        return userDept != null ? userDept.getDeptId() : null;
    }
}
