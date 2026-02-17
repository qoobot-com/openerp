package com.qoobot.qooerp.message.dto;

import lombok.Data;

@Data
public class TemplatePreviewDTO {
    private Long templateId;
    private java.util.Map<String, Object> variables;
}
