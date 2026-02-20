package com.qoobot.qooerp.report.dto;

import com.qoobot.qooerp.common.dto.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "报表历史查询DTO")
public class ReportHistoryQueryDTO extends BasePageQuery {
    @Schema(description = "报表ID")
    private Long reportId;
    
    @Schema(description = "快照时间开始")
    private String startTime;
    
    @Schema(description = "快照时间结束")
    private String endTime;
}
