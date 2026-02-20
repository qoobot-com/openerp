package com.qoobot.qooerp.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "报表订阅创建DTO")
public class ReportSubscriptionCreateDTO {
    @NotNull(message = "报表ID不能为空")
    @Schema(description = "报表ID", required = true)
    private Long reportId;
    
    @NotNull(message = "订阅用户ID不能为空")
    @Schema(description = "订阅用户ID", required = true)
    private Long userId;
    
    @NotNull(message = "订阅名称不能为空")
    @Schema(description = "订阅名称", required = true)
    private String subscriptionName;
    
    @NotNull(message = "调度配置不能为空")
    @Schema(description = "调度配置", required = true)
    private String scheduleConfig;
    
    @NotNull(message = "投递配置不能为空")
    @Schema(description = "投递配置", required = true)
    private String deliveryConfig;
    
    @Schema(description = "过滤条件")
    private String filterConfig;
}
