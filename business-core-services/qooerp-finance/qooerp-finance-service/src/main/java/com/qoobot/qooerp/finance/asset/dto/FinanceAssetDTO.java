package com.qoobot.qooerp.finance.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 固定资产DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "固定资产DTO")
public class FinanceAssetDTO {

    @Schema(description = "资产ID")
    private Long id;

    @Schema(description = "资产编码")
    private String assetCode;

    @Schema(description = "资产名称")
    private String assetName;

    @Schema(description = "资产分类")
    private String assetCategory;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "使用部门ID")
    private Long departmentId;

    @Schema(description = "使用部门名称")
    private String departmentName;

    @Schema(description = "存放地点")
    private String location;

    @Schema(description = "原值")
    private BigDecimal originalValue;

    @Schema(description = "预计净残值率")
    private BigDecimal salvageRate;

    @Schema(description = "预计净残值")
    private BigDecimal salvageValue;

    @Schema(description = "折旧方法（STRAIGHT-直线法 DOUBLE_DECLINING-双倍余额递减法 SUM_YEARS-年数总和法）")
    private String depreciationMethod;

    @Schema(description = "折旧年限（月）")
    private Integer depreciationYears;

    @Schema(description = "累计折旧")
    private BigDecimal accumulatedDepreciation;

    @Schema(description = "净值")
    private BigDecimal netValue;

    @Schema(description = "购入日期")
    private LocalDate purchaseDate;

    @Schema(description = "开始使用日期")
    private LocalDate useStartDate;

    @Schema(description = "资产状态（NORMAL-正常 SCRAPPED-报废 TRANSFERRED-调拨）")
    private String assetStatus;

    @Schema(description = "备注")
    private String remark;
}
