package com.qoobot.qooerp.message.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.*;

import java.util.List;

public interface MessageService {
    Long createMessage(MessageCreateDTO dto);
    Long sendMessage(MessageSendDTO dto);
    void markAsRead(Long messageId, Long receiverId);
    void batchMarkRead(BatchMarkReadDTO dto);
    void deleteMessage(Long id);
    void batchDelete(BatchDeleteDTO dto);
    MessageDTO getMessageById(Long id);
    Page<MessageDTO> queryMessages(MessageQueryDTO dto);
    List<UnreadMessageDTO> getUnreadMessages(Long receiverId);
    Long getUnreadCount(Long receiverId);
}
