package com.qoobot.qooerp.scm.logistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 物流查询DTO
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@Schema(description = "物流查询DTO")
public class LogisticsQueryDTO {

    @Schema(description = "物流单号")
    private String logisticsCode;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "关联单据号")
    private String relatedOrderCode;

    @Schema(description = "物流状态")
    private String logisticsStatus;

    @Schema(description = "物流公司")
    private String logisticsCompany;

    @Schema(description = "页码")
    private Integer current = 1;

    @Schema(description = "每页条数")
    private Integer size = 10;
}
