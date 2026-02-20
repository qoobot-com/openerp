package com.qoobot.qooerp.message.dto;

import lombok.Data;

@Data
public class MessageTemplateQueryDTO {
    private Long current;
    private Long size;
    private String templateNo;
    private String templateName;
    private Integer templateType;
    private Integer status;
}
