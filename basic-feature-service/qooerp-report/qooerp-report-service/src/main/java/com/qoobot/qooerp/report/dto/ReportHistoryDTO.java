package com.qoobot.qooerp.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "报表历史DTO")
public class ReportHistoryDTO {
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "报表ID")
    private Long reportId;
    
    @Schema(description = "快照数据")
    private String snapshotData;
    
    @Schema(description = "快照配置")
    private String snapshotConfig;
    
    @Schema(description = "快照时间")
    private LocalDateTime snapshotTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
}
