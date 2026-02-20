package com.qoobot.qooerp.message.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.*;

public interface MessageTemplateService {
    Long createTemplate(MessageTemplateCreateDTO dto);
    void updateTemplate(Long id, MessageTemplateCreateDTO dto);
    void deleteTemplate(Long id);
    MessageTemplateDTO getTemplateById(Long id);
    Page<MessageTemplateDTO> queryTemplates(MessageTemplateQueryDTO dto);
    MessageDTO previewTemplate(TemplatePreviewDTO dto);
}
