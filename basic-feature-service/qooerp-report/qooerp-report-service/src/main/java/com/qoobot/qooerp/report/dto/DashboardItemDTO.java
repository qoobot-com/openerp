package com.qoobot.qooerp.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "仪表盘项DTO")
public class DashboardItemDTO {
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "仪表盘ID")
    private Long dashboardId;
    
    @Schema(description = "报表ID")
    private Long reportId;
    
    @Schema(description = "项名称")
    private String itemName;
    
    @Schema(description = "位置配置")
    private String positionConfig;
    
    @Schema(description = "尺寸配置")
    private String sizeConfig;
    
    @Schema(description = "显示配置")
    private String displayConfig;
    
    @Schema(description = "刷新间隔(秒)")
    private Integer refreshInterval;
    
    @Schema(description = "排序")
    private Integer sortOrder;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}
