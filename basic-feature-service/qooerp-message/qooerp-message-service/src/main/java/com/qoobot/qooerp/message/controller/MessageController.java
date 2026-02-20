package com.qoobot.qooerp.message.controller;

import com.qoobot.qooerp.common.response.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.*;
import com.qoobot.qooerp.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/create")
    public Result<Long> createMessage(@RequestBody MessageCreateDTO dto) {
        Long id = messageService.createMessage(dto);
        return Result.success(id);
    }

    @PostMapping("/send")
    public Result<Long> sendMessage(@RequestBody MessageSendDTO dto) {
        Long id = messageService.sendMessage(dto);
        return Result.success(id);
    }

    @PostMapping("/mark-read/{messageId}")
    public Result<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId, 1L);
        return Result.success();
    }

    @PostMapping("/batch-mark-read")
    public Result<Void> batchMarkRead(@RequestBody BatchMarkReadDTO dto) {
        messageService.batchMarkRead(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return Result.success();
    }

    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody BatchDeleteDTO dto) {
        messageService.batchDelete(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<MessageDTO> getMessageById(@PathVariable Long id) {
        MessageDTO dto = messageService.getMessageById(id);
        return Result.success(dto);
    }

    @PostMapping("/list")
    public Result<Page<MessageDTO>> queryMessages(@RequestBody MessageQueryDTO dto) {
        Page<MessageDTO> page = messageService.queryMessages(dto);
        return Result.success(page);
    }

    @GetMapping("/unread/{receiverId}")
    public Result<List<UnreadMessageDTO>> getUnreadMessages(@PathVariable Long receiverId) {
        List<UnreadMessageDTO> list = messageService.getUnreadMessages(receiverId);
        return Result.success(list);
    }

    @GetMapping("/unread-count/{receiverId}")
    public Result<Long> getUnreadCount(@PathVariable Long receiverId) {
        Long count = messageService.getUnreadCount(receiverId);
        return Result.success(count);
    }
}
