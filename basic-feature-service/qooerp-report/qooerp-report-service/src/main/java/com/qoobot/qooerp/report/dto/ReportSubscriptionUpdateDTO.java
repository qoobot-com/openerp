package com.qoobot.qooerp.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报表订阅更新DTO")
public class ReportSubscriptionUpdateDTO {
    @Schema(description = "订阅名称")
    private String subscriptionName;
    
    @Schema(description = "调度配置")
    private String scheduleConfig;
    
    @Schema(description = "投递配置")
    private String deliveryConfig;
    
    @Schema(description = "过滤条件")
    private String filterConfig;
    
    @Schema(description = "状态")
    private Integer status;
}
