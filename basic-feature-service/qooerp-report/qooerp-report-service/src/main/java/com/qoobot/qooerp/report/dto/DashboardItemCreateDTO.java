package com.qoobot.qooerp.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "仪表盘项创建DTO")
public class DashboardItemCreateDTO {
    @NotNull(message = "仪表盘ID不能为空")
    @Schema(description = "仪表盘ID", required = true)
    private Long dashboardId;
    
    @NotNull(message = "报表ID不能为空")
    @Schema(description = "报表ID", required = true)
    private Long reportId;
    
    @NotNull(message = "项名称不能为空")
    @Schema(description = "项名称", required = true)
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
}
