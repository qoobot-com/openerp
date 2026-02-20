package com.qoobot.qooerp.finance.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 资产折旧DTO
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@Schema(description = "资产折旧DTO")
public class AssetDepreciationDTO {

    @Schema(description = "折旧记录ID")
    private Long id;

    @Schema(description = "资产ID")
    private Long assetId;

    @Schema(description = "资产编码")
    private String assetCode;

    @Schema(description = "资产名称")
    private String assetName;

    @Schema(description = "折旧年度")
    private Integer depreciationYear;

    @Schema(description = "折旧期间")
    private String depreciationPeriod;

    @Schema(description = "折旧金额")
    private BigDecimal depreciationAmount;

    @Schema(description = "累计折旧")
    private BigDecimal accumulatedDepreciation;

    @Schema(description = "折旧后净值")
    private BigDecimal netValue;

    @Schema(description = "折旧日期")
    private LocalDate depreciationDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private String createdBy;

    @Schema(description = "创建时间")
    private String createdTime;
}
