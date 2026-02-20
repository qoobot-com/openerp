package com.qoobot.qooerp.monitor.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class AlertHandleDTO {
    @NotNull(message = "告警ID不能为空")
    private Long alertId;

    @NotNull(message = "处理人ID不能为空")
    private Long handlerId;

    @NotBlank(message = "处理动作不能为空")
    private String handleAction;

    private String handleRemark;
}
