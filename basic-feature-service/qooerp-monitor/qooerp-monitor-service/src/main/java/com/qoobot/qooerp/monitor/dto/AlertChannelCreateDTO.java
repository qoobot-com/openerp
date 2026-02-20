package com.qoobot.qooerp.monitor.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AlertChannelCreateDTO {
    @NotBlank(message = "渠道名称不能为空")
    private String channelName;

    @NotBlank(message = "渠道类型不能为空")
    private String channelType;

    private String channelConfig;
    private Boolean enabled;
    private String description;
}
