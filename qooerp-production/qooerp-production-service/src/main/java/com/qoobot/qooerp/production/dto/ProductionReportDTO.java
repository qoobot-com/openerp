package com.qoobot.qooerp.production.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 生产报工DTO
 */
@Data
public class ProductionReportDTO {

    @NotNull(message = "报工单号不能为空")
    private String reportNo;

    @NotNull(message = "生产订单ID不能为空")
    private Long orderId;

    private Long processId;

    private String processName;

    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "报工数量不能为空")
    private BigDecimal reportQuantity;

    @NotNull(message = "合格数量不能为空")
    private BigDecimal qualifiedQuantity;

    private BigDecimal rejectQuantity;

    private BigDecimal workTime;

    private Long workerId;

    private String workerName;

    private Long equipmentId;

    private String equipmentName;

    private LocalDate reportDate;

    private String remark;
}
