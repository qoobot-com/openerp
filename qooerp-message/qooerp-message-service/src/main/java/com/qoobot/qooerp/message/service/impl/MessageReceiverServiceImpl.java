package com.qoobot.qooerp.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.MessageReceiverDTO;
import com.qoobot.qooerp.message.dto.MessageReceiverQueryDTO;
import com.qoobot.qooerp.message.entity.MessageReceiver;
import com.qoobot.qooerp.message.mapper.MessageReceiverMapper;
import com.qoobot.qooerp.message.service.MessageReceiverService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageReceiverServiceImpl implements MessageReceiverService {

    private final MessageReceiverMapper receiverMapper;

    @Override
    public Page<MessageReceiverDTO> queryReceivers(MessageReceiverQueryDTO dto) {
        IPage<MessageReceiver> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<MessageReceiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getMessageId() != null, MessageReceiver::getMessageId, dto.getMessageId())
                .eq(dto.getReceiverId() != null, MessageReceiver::getReceiverId, dto.getReceiverId())
                .eq(dto.getReadStatus() != null, MessageReceiver::getReadStatus, dto.getReadStatus())
                .orderByDesc(MessageReceiver::getCreateTime);
        IPage<MessageReceiver> result = receiverMapper.selectPage(page, wrapper);

        Page<MessageReceiverDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    private MessageReceiverDTO convertToDTO(MessageReceiver entity) {
        MessageReceiverDTO dto = new MessageReceiverDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
