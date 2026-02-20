package com.qoobot.qooerp.monitor.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ServiceCheckDTO {
    @NotBlank(message = "服务名称不能为空")
    private String serviceName;
}
