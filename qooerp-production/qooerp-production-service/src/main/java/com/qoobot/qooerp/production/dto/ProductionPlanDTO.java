package com.qoobot.qooerp.production.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 生产计划DTO
 */
@Data
public class ProductionPlanDTO {

    @NotNull(message = "计划编号不能为空")
    private String planNo;

    @NotBlank(message = "计划名称不能为空")
    private String planName;

    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "计划数量不能为空")
    private BigDecimal planQuantity;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer priority;

    private String remark;
}
