package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageTemplateDTO {
    private Long id;
    private String templateNo;
    private String templateName;
    private Integer templateType;
    private String titleTemplate;
    private String contentTemplate;
    private String variables;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
