package com.qoobot.qooerp.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.notify.dto.NotifyRecordDTO;
import com.qoobot.qooerp.notify.dto.NotifySendDTO;
import com.qoobot.qooerp.notify.entity.NotifyRecord;
import com.qoobot.qooerp.notify.entity.NotifyTemplate;
import com.qoobot.qooerp.notify.enums.NotifyStatusEnum;
import com.qoobot.qooerp.notify.enums.NotifyTypeEnum;
import com.qoobot.qooerp.notify.mapper.NotifyRecordMapper;
import com.qoobot.qooerp.notify.mapper.NotifyTemplateMapper;
import com.qoobot.qooerp.notify.service.NotifyService;
import com.qoobot.qooerp.notify.sender.*;
import com.qoobot.qooerp.notify.util.TemplateParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final NotifyRecordMapper recordMapper;
    private final NotifyTemplateMapper templateMapper;
    private final EmailSender emailSender;
    private final SmsSender smsSender;
    private final InternalSender internalSender;
    private final PushSender pushSender;
    private final WebhookSender webhookSender;
    private final TemplateParser templateParser;
    private final ObjectMapper objectMapper;

    @Override
    public NotifyRecordDTO send(NotifySendDTO dto) {
        String content = dto.getContent();

        // 如果使用模板，解析模板
        if (dto.getTemplateId() != null) {
            NotifyTemplate template = templateMapper.selectById(dto.getTemplateId());
            if (template == null) {
                throw new RuntimeException("模板不存在");
            }

            String parsedContent = templateParser.parse(template.getContent(), dto.getVariables());
            content = parsedContent;

            if (dto.getTitle() == null) {
                dto.setTitle(template.getSubject());
            }
        }

        // 创建记录
        NotifyRecord record = new NotifyRecord();
        record.setNotifyNo(generateNotifyNo());
        record.setType(dto.getType());
        record.setReceiver(dto.getReceiver());
        record.setTitle(dto.getTitle());
        record.setContent(content);
        record.setTemplateId(dto.getTemplateId());
        record.setStatus(NotifyStatusEnum.SENDING);
        record.setTenantId(TenantContextHolder.getTenantId());

        recordMapper.insert(record);

        // 发送通知
        boolean success = doSend(dto, content);

        // 更新状态
        record.setStatus(success ? NotifyStatusEnum.SUCCESS : NotifyStatusEnum.FAILED);
        record.setSentTime(LocalDateTime.now());
        record.setRetryCount(success ? 0 : 1);

        if (!success) {
            record.setErrorMsg("发送失败");
            record.setRetryTime(LocalDateTime.now().plusMinutes(1));
        }

        recordMapper.updateById(record);

        return convertToDTO(record);
    }

    @Override
    public void sendBatch(NotifySendDTO dto, String[] receivers) {
        for (String receiver : receivers) {
            NotifySendDTO copy = new NotifySendDTO();
            BeanUtils.copyProperties(dto, copy);
            copy.setReceiver(receiver);
            send(copy);
        }
    }

    @Override
    public void resend(NotifyRecord record) {
        NotifySendDTO dto = new NotifySendDTO();
        dto.setType(record.getType());
        dto.setReceiver(record.getReceiver());
        dto.setTitle(record.getTitle());
        dto.setContent(record.getContent());

        boolean success = doSend(dto, record.getContent());

        // 更新状态
        record.setStatus(success ? NotifyStatusEnum.SUCCESS : NotifyStatusEnum.FAILED);
        record.setSentTime(LocalDateTime.now());

        if (!success) {
            int retryCount = record.getRetryCount() + 1;
            record.setRetryCount(retryCount);
            record.setErrorMsg("重试发送失败");
            // 指数退避策略：1分钟、5分钟、15分钟
            long delayMinutes = (long) Math.pow(5, retryCount - 1);
            record.setRetryTime(LocalDateTime.now().plusMinutes(delayMinutes));
        }

        recordMapper.updateById(record);
    }

    private boolean doSend(NotifySendDTO dto, String content) {
        NotifyTypeEnum type = dto.getType();

        try {
            switch (type) {
                case EMAIL:
                    return emailSender.send(dto.getReceiver(), dto.getTitle(), content);

                case SMS:
                    return smsSender.send(dto.getReceiver(), content);

                case INTERNAL:
                    // 站内通知 receiver 为 userId
                    Long userId = Long.parseLong(dto.getReceiver());
                    return internalSender.send(userId, dto.getTitle(), content);

                case PUSH:
                    return pushSender.send(dto.getReceiver(), dto.getTitle(), content);

                case WEBHOOK:
                    Map<String, Object> data = objectMapper.readValue(content, Map.class);
                    return webhookSender.send(dto.getReceiver(), data);

                default:
                    log.warn("不支持的通知类型: {}", type);
                    return false;
            }
        } catch (Exception e) {
            log.error("通知发送失败: type={}, receiver={}", type, dto.getReceiver(), e);
            return false;
        }
    }

    private String generateNotifyNo() {
        return "NOTIFY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private NotifyRecordDTO convertToDTO(NotifyRecord entity) {
        NotifyRecordDTO dto = new NotifyRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
