package com.qoobot.qooerp.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.entity.MonitorAlertRule;
import com.qoobot.qooerp.monitor.mapper.MonitorAlertRuleMapper;
import com.qoobot.qooerp.monitor.service.MonitorAlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorAlertRuleServiceImpl implements MonitorAlertRuleService {

    private final MonitorAlertRuleMapper ruleMapper;

    @Override
    public Long createRule(AlertRuleCreateDTO dto) {
        MonitorAlertRule entity = new MonitorAlertRule();
        BeanUtils.copyProperties(dto, entity);
        entity.setTenantId(TenantContextHolder.getTenantId());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
        ruleMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateRule(Long id, AlertRuleUpdateDTO dto) {
        MonitorAlertRule entity = ruleMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("规则不存在");
        }
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        ruleMapper.updateById(entity);
    }

    @Override
    public void deleteRule(Long id) {
        ruleMapper.deleteById(id);
    }

    @Override
    public Page<AlertRuleDTO> listRules(AlertRuleQueryDTO dto) {
        LambdaQueryWrapper<MonitorAlertRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorAlertRule::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getRuleName() != null) {
            wrapper.like(MonitorAlertRule::getRuleName, dto.getRuleName());
        }
        if (dto.getRuleType() != null) {
            wrapper.eq(MonitorAlertRule::getRuleType, dto.getRuleType());
        }
        if (dto.getAlertLevel() != null) {
            wrapper.eq(MonitorAlertRule::getAlertLevel, dto.getAlertLevel());
        }
        if (dto.getEnabled() != null) {
            wrapper.eq(MonitorAlertRule::getEnabled, dto.getEnabled());
        }

        wrapper.orderByDesc(MonitorAlertRule::getCreateTime);

        Page<MonitorAlertRule> page = ruleMapper.selectPage(dto, wrapper);
        Page<AlertRuleDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return result;
    }

    @Override
    public AlertRuleDTO getRule(Long id) {
        MonitorAlertRule entity = ruleMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("规则不存在");
        }
        return convertToDTO(entity);
    }

    @Override
    public void enableRule(Long id) {
        MonitorAlertRule entity = ruleMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("规则不存在");
        }
        entity.setEnabled(true);
        entity.setUpdateTime(LocalDateTime.now());
        ruleMapper.updateById(entity);
    }

    @Override
    public void disableRule(Long id) {
        MonitorAlertRule entity = ruleMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("规则不存在");
        }
        entity.setEnabled(false);
        entity.setUpdateTime(LocalDateTime.now());
        ruleMapper.updateById(entity);
    }

    private AlertRuleDTO convertToDTO(MonitorAlertRule entity) {
        AlertRuleDTO dto = new AlertRuleDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
