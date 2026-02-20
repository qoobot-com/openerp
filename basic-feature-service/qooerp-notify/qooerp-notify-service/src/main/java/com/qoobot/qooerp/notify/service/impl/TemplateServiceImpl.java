package com.qoobot.qooerp.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.notify.dto.*;
import com.qoobot.qooerp.notify.entity.NotifyRecord;
import com.qoobot.qooerp.notify.entity.NotifyTemplate;
import com.qoobot.qooerp.notify.mapper.NotifyRecordMapper;
import com.qoobot.qooerp.notify.mapper.NotifyTemplateMapper;
import com.qoobot.qooerp.notify.service.TemplateService;
import com.qoobot.qooerp.notify.util.TemplateParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板服务实现
 */
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final NotifyTemplateMapper templateMapper;
    private final NotifyRecordMapper recordMapper;
    private final TemplateParser templateParser;

    @Override
    public Long create(TemplateCreateDTO dto) {
        NotifyTemplate template = new NotifyTemplate();
        BeanUtils.copyProperties(dto, template);
        template.setTenantId(TenantContextHolder.getTenantId());
        template.setStatus(1);
        templateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void update(TemplateUpdateDTO dto) {
        NotifyTemplate template = templateMapper.selectById(dto.getId());
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }

        BeanUtils.copyProperties(dto, template);
        template.setUpdateTime(java.time.LocalDateTime.now());
        templateMapper.updateById(template);
    }

    @Override
    public void delete(Long id) {
        NotifyTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }

        templateMapper.deleteById(id);
    }

    @Override
    public TemplateDTO get(Long id) {
        NotifyTemplate template = templateMapper.selectById(id);
        if (template == null) {
            return null;
        }

        TemplateDTO dto = new TemplateDTO();
        BeanUtils.copyProperties(template, dto);
        return dto;
    }

    @Override
    public Page<TemplateDTO> page(TemplateQueryDTO dto) {
        LambdaQueryWrapper<NotifyTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyTemplate::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getTemplateCode() != null) {
            wrapper.like(NotifyTemplate::getTemplateCode, dto.getTemplateCode());
        }
        if (dto.getTemplateName() != null) {
            wrapper.like(NotifyTemplate::getTemplateName, dto.getTemplateName());
        }
        if (dto.getCategory() != null) {
            wrapper.eq(NotifyTemplate::getCategory, dto.getCategory());
        }
        if (dto.getType() != null) {
            wrapper.eq(NotifyTemplate::getType, dto.getType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(NotifyTemplate::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(NotifyTemplate::getCreateTime);

        Page<NotifyTemplate> page = new Page<>(dto.getCurrent(), dto.getSize());
        page = templateMapper.selectPage(page, wrapper);

        Page<TemplateDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToDTO).toList());
        return result;
    }

    @Override
    public Map<String, Object> preview(TemplatePreviewDTO dto) {
        NotifyTemplate template = templateMapper.selectById(dto.getTemplateId());
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }

        Map<String, Object> result = new HashMap<>();

        // 解析主题
        if (template.getSubject() != null) {
            String parsedSubject = templateParser.parse(template.getSubject(), dto.getVariables());
            result.put("subject", parsedSubject);
        }

        // 解析内容
        String parsedContent = templateParser.parse(template.getContent(), dto.getVariables());
        result.put("content", parsedContent);

        result.put("templateCode", template.getTemplateCode());
        result.put("templateName", template.getTemplateName());

        return result;
    }

    @Override
    public Page<NotifyRecordDTO> pageRecords(NotifyRecordQueryDTO dto) {
        LambdaQueryWrapper<NotifyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyRecord::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getNotifyNo() != null) {
            wrapper.like(NotifyRecord::getNotifyNo, dto.getNotifyNo());
        }
        if (dto.getType() != null) {
            wrapper.eq(NotifyRecord::getType, dto.getType());
        }
        if (dto.getReceiver() != null) {
            wrapper.like(NotifyRecord::getReceiver, dto.getReceiver());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(NotifyRecord::getStatus, dto.getStatus());
        }
        if (dto.getTemplateId() != null) {
            wrapper.eq(NotifyRecord::getTemplateId, dto.getTemplateId());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(NotifyRecord::getCreateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(NotifyRecord::getCreateTime, dto.getEndTime());
        }

        wrapper.orderByDesc(NotifyRecord::getCreateTime);

        Page<NotifyRecord> page = new Page<>(dto.getCurrent(), dto.getSize());
        page = recordMapper.selectPage(page, wrapper);

        Page<NotifyRecordDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToDTO).toList());
        return result;
    }

    @Override
    public NotifyRecordDTO getRecord(Long id) {
        NotifyRecord record = recordMapper.selectById(id);
        if (record == null) {
            return null;
        }
        return convertToDTO(record);
    }

    private NotifyRecordDTO convertToDTO(NotifyRecord entity) {
        NotifyRecordDTO dto = new NotifyRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private TemplateDTO convertToDTO(NotifyTemplate entity) {
        TemplateDTO dto = new TemplateDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
