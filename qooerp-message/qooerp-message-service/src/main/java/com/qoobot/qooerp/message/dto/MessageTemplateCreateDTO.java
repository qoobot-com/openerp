package com.qoobot.qooerp.message.dto;

import lombok.Data;

@Data
public class MessageTemplateCreateDTO {
    private String templateName;
    private Integer templateType;
    private String titleTemplate;
    private String contentTemplate;
    private String variables;
}
