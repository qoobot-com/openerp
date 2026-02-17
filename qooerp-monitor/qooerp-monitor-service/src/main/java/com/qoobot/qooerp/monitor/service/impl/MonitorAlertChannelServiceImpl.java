package com.qoobot.qooerp.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.entity.MonitorAlertChannel;
import com.qoobot.qooerp.monitor.mapper.MonitorAlertChannelMapper;
import com.qoobot.qooerp.monitor.service.MonitorAlertChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorAlertChannelServiceImpl implements MonitorAlertChannelService {

    private final MonitorAlertChannelMapper channelMapper;

    @Override
    public Long createChannel(AlertChannelCreateDTO dto) {
        MonitorAlertChannel entity = new MonitorAlertChannel();
        BeanUtils.copyProperties(dto, entity);
        entity.setTenantId(TenantContextHolder.getTenantId());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
        channelMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateChannel(Long id, AlertChannelUpdateDTO dto) {
        MonitorAlertChannel entity = channelMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("渠道不存在");
        }
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        channelMapper.updateById(entity);
    }

    @Override
    public void deleteChannel(Long id) {
        channelMapper.deleteById(id);
    }

    @Override
    public List<AlertChannelDTO> listChannels() {
        LambdaQueryWrapper<MonitorAlertChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorAlertChannel::getTenantId, TenantContextHolder.getTenantId());
        wrapper.orderByDesc(MonitorAlertChannel::getCreateTime);
        List<MonitorAlertChannel> entities = channelMapper.selectList(wrapper);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public AlertChannelDTO getChannel(Long id) {
        MonitorAlertChannel entity = channelMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("渠道不存在");
        }
        return convertToDTO(entity);
    }

    private AlertChannelDTO convertToDTO(MonitorAlertChannel entity) {
        AlertChannelDTO dto = new AlertChannelDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
