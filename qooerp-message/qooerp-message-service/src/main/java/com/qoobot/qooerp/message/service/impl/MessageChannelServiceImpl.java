package com.qoobot.qooerp.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.MessageChannelCreateDTO;
import com.qoobot.qooerp.message.dto.MessageChannelDTO;
import com.qoobot.qooerp.message.dto.MessageChannelQueryDTO;
import com.qoobot.qooerp.message.entity.MessageChannel;
import com.qoobot.qooerp.message.mapper.MessageChannelMapper;
import com.qoobot.qooerp.message.service.MessageChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageChannelServiceImpl implements MessageChannelService {

    private final MessageChannelMapper channelMapper;

    @Override
    public Long createChannel(MessageChannelCreateDTO dto) {
        LambdaQueryWrapper<MessageChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageChannel::getChannelCode, dto.getChannelCode())
                .eq(MessageChannel::getTenantId, getCurrentTenantId());
        if (channelMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("渠道编码已存在");
        }

        MessageChannel channel = new MessageChannel();
        BeanUtils.copyProperties(dto, channel);
        channel.setTenantId(getCurrentTenantId());
        channel.setStatus(1);
        channel.setCreateTime(LocalDateTime.now());
        channelMapper.insert(channel);

        return channel.getId();
    }

    @Override
    public void updateChannel(Long id, MessageChannelCreateDTO dto) {
        MessageChannel channel = channelMapper.selectById(id);
        if (channel == null) {
            throw new RuntimeException("渠道不存在");
        }
        BeanUtils.copyProperties(dto, channel);
        channel.setUpdateTime(LocalDateTime.now());
        channelMapper.updateById(channel);
    }

    @Override
    public void deleteChannel(Long id) {
        channelMapper.deleteById(id);
    }

    @Override
    public void enableChannel(Long id) {
        MessageChannel channel = channelMapper.selectById(id);
        if (channel == null) {
            throw new RuntimeException("渠道不存在");
        }
        channel.setStatus(1);
        channel.setUpdateTime(LocalDateTime.now());
        channelMapper.updateById(channel);
    }

    @Override
    public void disableChannel(Long id) {
        MessageChannel channel = channelMapper.selectById(id);
        if (channel == null) {
            throw new RuntimeException("渠道不存在");
        }
        channel.setStatus(0);
        channel.setUpdateTime(LocalDateTime.now());
        channelMapper.updateById(channel);
    }

    @Override
    public MessageChannelDTO getChannelById(Long id) {
        MessageChannel channel = channelMapper.selectById(id);
        if (channel == null) {
            throw new RuntimeException("渠道不存在");
        }
        return convertToDTO(channel);
    }

    @Override
    public Page<MessageChannelDTO> queryChannels(MessageChannelQueryDTO dto) {
        IPage<MessageChannel> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<MessageChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageChannel::getTenantId, getCurrentTenantId())
                .like(dto.getChannelCode() != null, MessageChannel::getChannelCode, dto.getChannelCode())
                .like(dto.getChannelName() != null, MessageChannel::getChannelName, dto.getChannelName())
                .eq(dto.getChannelType() != null, MessageChannel::getChannelType, dto.getChannelType())
                .eq(dto.getStatus() != null, MessageChannel::getStatus, dto.getStatus())
                .orderByDesc(MessageChannel::getCreateTime);
        IPage<MessageChannel> result = channelMapper.selectPage(page, wrapper);

        Page<MessageChannelDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    private MessageChannelDTO convertToDTO(MessageChannel entity) {
        MessageChannelDTO dto = new MessageChannelDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private Long getCurrentTenantId() {
        return 1L;
    }
}
