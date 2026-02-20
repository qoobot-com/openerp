package com.qoobot.qooerp.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "报表订阅DTO")
public class ReportSubscriptionDTO {
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "报表ID")
    private Long reportId;
    
    @Schema(description = "订阅用户ID")
    private Long userId;
    
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
    
    @Schema(description = "最后运行时间")
    private LocalDateTime lastRunTime;
    
    @Schema(description = "下次运行时间")
    private LocalDateTime nextRunTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}
