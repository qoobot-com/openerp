package com.qoobot.qooerp.report.dto;

import com.qoobot.qooerp.common.dto.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "报表订阅查询DTO")
public class ReportSubscriptionQueryDTO extends BasePageQuery {
    @Schema(description = "报表ID")
    private Long reportId;
    
    @Schema(description = "订阅用户ID")
    private Long userId;
    
    @Schema(description = "订阅名称")
    private String subscriptionName;
    
    @Schema(description = "状态")
    private Integer status;
}
