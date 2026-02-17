package com.qoobot.qooerp.monitor.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MonitorServiceCreateDTO {
    @NotBlank(message = "服务名称不能为空")
    private String serviceName;

    @NotBlank(message = "服务地址不能为空")
    private String serviceAddress;

    @NotNull(message = "服务端口不能为空")
    private Integer servicePort;

    private Integer status;
    private Integer healthStatus;
}
