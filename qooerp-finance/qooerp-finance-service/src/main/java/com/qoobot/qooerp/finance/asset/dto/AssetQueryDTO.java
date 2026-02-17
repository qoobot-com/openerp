package com.qoobot.qooerp.finance.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 固定资产查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "固定资产查询DTO")
public class AssetQueryDTO {

    @Schema(description = "资产编码（模糊查询）")
    private String assetCode;

    @Schema(description = "资产名称（模糊查询）")
    private String assetName;

    @Schema(description = "资产分类")
    private String assetCategory;

    @Schema(description = "使用部门ID")
    private Long departmentId;

    @Schema(description = "购入开始日期")
    private LocalDate purchaseStartDate;

    @Schema(description = "购入结束日期")
    private LocalDate purchaseEndDate;

    @Schema(description = "资产状态")
    private String assetStatus;

    @Schema(description = "页码")
    private Integer pageNo = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}
