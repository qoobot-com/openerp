package com.qoobot.qooerp.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.*;
import com.qoobot.qooerp.message.entity.Message;
import com.qoobot.qooerp.message.entity.MessageReceiver;
import com.qoobot.qooerp.message.mapper.MessageMapper;
import com.qoobot.qooerp.message.mapper.MessageReceiverMapper;
import com.qoobot.qooerp.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final MessageReceiverMapper receiverMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMessage(MessageCreateDTO dto) {
        String messageNo = generateMessageNo();

        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        message.setMessageNo(messageNo);
        message.setTenantId(getCurrentTenantId());
        message.setStatus(1);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);

        return message.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessage(MessageSendDTO dto) {
        String messageNo = generateMessageNo();

        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        message.setMessageNo(messageNo);
        message.setTenantId(getCurrentTenantId());
        message.setStatus(2);
        message.setSendTime(LocalDateTime.now());
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);

        for (Long receiverId : dto.getReceiverIds()) {
            MessageReceiver receiver = new MessageReceiver();
            receiver.setMessageId(message.getId());
            receiver.setReceiverId(receiverId);
            receiver.setReadStatus(0);
            receiver.setTenantId(getCurrentTenantId());
            receiver.setCreateTime(LocalDateTime.now());
            receiverMapper.insert(receiver);
        }

        return message.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId, Long receiverId) {
        LambdaQueryWrapper<MessageReceiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReceiver::getMessageId, messageId)
                .eq(MessageReceiver::getReceiverId, receiverId);
        MessageReceiver receiver = receiverMapper.selectOne(wrapper);

        if (receiver != null) {
            receiver.setReadStatus(1);
            receiver.setReadTime(LocalDateTime.now());
            receiverMapper.updateById(receiver);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkRead(BatchMarkReadDTO dto) {
        LambdaQueryWrapper<MessageReceiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MessageReceiver::getMessageId, dto.getMessageIds())
                .eq(MessageReceiver::getReceiverId, getCurrentUserId());

        List<MessageReceiver> receivers = receiverMapper.selectList(wrapper);
        for (MessageReceiver receiver : receivers) {
            receiver.setReadStatus(1);
            receiver.setReadTime(LocalDateTime.now());
            receiverMapper.updateById(receiver);
        }
    }

    @Override
    public void deleteMessage(Long id) {
        messageMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(BatchDeleteDTO dto) {
        for (Long id : dto.getMessageIds()) {
            messageMapper.deleteById(id);
        }
    }

    @Override
    public MessageDTO getMessageById(Long id) {
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new RuntimeException("消息不存在");
        }

        MessageDTO dto = convertToDTO(message);

        LambdaQueryWrapper<MessageReceiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReceiver::getMessageId, id);
        List<MessageReceiver> receivers = receiverMapper.selectList(wrapper);
        dto.setReceivers(receivers.stream().map(this::convertReceiverToDTO).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public Page<MessageDTO> queryMessages(MessageQueryDTO dto) {
        IPage<Message> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getTenantId, getCurrentTenantId())
                .like(dto.getMessageNo() != null, Message::getMessageNo, dto.getMessageNo())
                .eq(dto.getType() != null, Message::getType, dto.getType())
                .eq(dto.getStatus() != null, Message::getStatus, dto.getStatus())
                .ge(dto.getStartDate() != null, Message::getCreateTime, dto.getStartDate())
                .le(dto.getEndDate() != null, Message::getCreateTime, dto.getEndDate())
                .orderByDesc(Message::getCreateTime);
        IPage<Message> result = messageMapper.selectPage(page, wrapper);

        Page<MessageDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public List<UnreadMessageDTO> getUnreadMessages(Long receiverId) {
        LambdaQueryWrapper<MessageReceiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReceiver::getReceiverId, receiverId)
                .eq(MessageReceiver::getReadStatus, 0);
        List<MessageReceiver> receivers = receiverMapper.selectList(wrapper);

        List<Long> messageIds = receivers.stream().map(MessageReceiver::getMessageId).collect(Collectors.toList());

        if (messageIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.in(Message::getId, messageIds);
        List<Message> messages = messageMapper.selectList(messageWrapper);

        return messages.stream().map(this::convertToUnreadDTO).collect(Collectors.toList());
    }

    @Override
    public Long getUnreadCount(Long receiverId) {
        LambdaQueryWrapper<MessageReceiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReceiver::getReceiverId, receiverId)
                .eq(MessageReceiver::getReadStatus, 0);
        return receiverMapper.selectCount(wrapper);
    }

    private MessageDTO convertToDTO(Message entity) {
        MessageDTO dto = new MessageDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private MessageReceiverDTO convertReceiverToDTO(MessageReceiver entity) {
        MessageReceiverDTO dto = new MessageReceiverDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private UnreadMessageDTO convertToUnreadDTO(Message entity) {
        UnreadMessageDTO dto = new UnreadMessageDTO();
        dto.setId(entity.getId());
        dto.setMessageNo(entity.getMessageNo());
        dto.setTitle(entity.getTitle());
        dto.setType(entity.getType());
        dto.setPriority(entity.getPriority());
        dto.setSenderName(entity.getSenderName());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }

    private String generateMessageNo() {
        return "MSG" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private Long getCurrentTenantId() {
        return 1L;
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
