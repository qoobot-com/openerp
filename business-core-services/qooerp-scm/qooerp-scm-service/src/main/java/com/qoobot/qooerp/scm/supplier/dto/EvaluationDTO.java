package com.qoobot.qooerp.scm.supplier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评估DTO
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@Schema(description = "评估DTO")
public class EvaluationDTO {

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "质量评分（0-100）")
    @NotNull(message = "质量评分不能为空")
    @Min(value = 0, message = "质量评分不能小于0")
    @Max(value = 100, message = "质量评分不能大于100")
    private Integer qualityScore;

    @Schema(description = "交付评分（0-100）")
    @NotNull(message = "交付评分不能为空")
    @Min(value = 0, message = "交付评分不能小于0")
    @Max(value = 100, message = "交付评分不能大于100")
    private Integer deliveryScore;

    @Schema(description = "服务评分（0-100）")
    @NotNull(message = "服务评分不能为空")
    @Min(value = 0, message = "服务评分不能小于0")
    @Max(value = 100, message = "服务评分不能大于100")
    private Integer serviceScore;

    @Schema(description = "价格评分（0-100）")
    @NotNull(message = "价格评分不能为空")
    @Min(value = 0, message = "价格评分不能小于0")
    @Max(value = 100, message = "价格评分不能大于100")
    private Integer priceScore;

    @Schema(description = "评估意见")
    private String evaluationRemark;
}
