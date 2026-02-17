package com.qoobot.qooerp.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.MessageDTO;
import com.qoobot.qooerp.message.dto.MessageTemplateCreateDTO;
import com.qoobot.qooerp.message.dto.MessageTemplateDTO;
import com.qoobot.qooerp.message.dto.MessageTemplateQueryDTO;
import com.qoobot.qooerp.message.entity.MessageTemplate;
import com.qoobot.qooerp.message.mapper.MessageTemplateMapper;
import com.qoobot.qooerp.message.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageTemplateServiceImpl implements MessageTemplateService {

    private final MessageTemplateMapper templateMapper;

    @Override
    public Long createTemplate(MessageTemplateCreateDTO dto) {
        LambdaQueryWrapper<MessageTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageTemplate::getTemplateNo, generateTemplateNo());
        if (templateMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("模板编号已存在");
        }

        MessageTemplate template = new MessageTemplate();
        BeanUtils.copyProperties(dto, template);
        template.setTemplateNo(generateTemplateNo());
        template.setTenantId(getCurrentTenantId());
        template.setStatus(1);
        template.setCreateBy(getCurrentUserId());
        template.setCreateTime(LocalDateTime.now());
        templateMapper.insert(template);

        return template.getId();
    }

    @Override
    public void updateTemplate(Long id, MessageTemplateCreateDTO dto) {
        MessageTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        BeanUtils.copyProperties(dto, template);
        template.setUpdateBy(getCurrentUserId());
        template.setUpdateTime(LocalDateTime.now());
        templateMapper.updateById(template);
    }

    @Override
    public void deleteTemplate(Long id) {
        templateMapper.deleteById(id);
    }

    @Override
    public MessageTemplateDTO getTemplateById(Long id) {
        MessageTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        return convertToDTO(template);
    }

    @Override
    public Page<MessageTemplateDTO> queryTemplates(MessageTemplateQueryDTO dto) {
        IPage<MessageTemplate> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<MessageTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageTemplate::getTenantId, getCurrentTenantId())
                .like(dto.getTemplateNo() != null, MessageTemplate::getTemplateNo, dto.getTemplateNo())
                .like(dto.getTemplateName() != null, MessageTemplate::getTemplateName, dto.getTemplateName())
                .eq(dto.getTemplateType() != null, MessageTemplate::getTemplateType, dto.getTemplateType())
                .eq(dto.getStatus() != null, MessageTemplate::getStatus, dto.getStatus())
                .orderByDesc(MessageTemplate::getCreateTime);
        IPage<MessageTemplate> result = templateMapper.selectPage(page, wrapper);

        Page<MessageTemplateDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public MessageDTO previewTemplate(com.qoobot.qooerp.message.dto.TemplatePreviewDTO dto) {
        MessageTemplate template = templateMapper.selectById(dto.getTemplateId());
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }

        String title = replaceVariables(template.getTitleTemplate(), dto.getVariables());
        String content = replaceVariables(template.getContentTemplate(), dto.getVariables());

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTitle(title);
        messageDTO.setContent(content);

        return messageDTO;
    }

    private String replaceVariables(String template, Map<String, Object> variables) {
        if (variables == null || template == null) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result = result.replace("${" + key + "}", value != null ? value.toString() : "");
        }
        return result;
    }

    private MessageTemplateDTO convertToDTO(MessageTemplate entity) {
        MessageTemplateDTO dto = new MessageTemplateDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateTemplateNo() {
        return "TPL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private Long getCurrentTenantId() {
        return 1L;
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
