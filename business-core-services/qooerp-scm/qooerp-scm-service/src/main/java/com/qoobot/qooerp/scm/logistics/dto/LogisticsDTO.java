package com.qoobot.qooerp.scm.logistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 物流DTO
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@Schema(description = "物流DTO")
public class LogisticsDTO {

    @Schema(description = "物流ID")
    private Long id;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "关联单据ID")
    private Long relatedOrderId;

    @Schema(description = "关联单据号")
    private String relatedOrderCode;

    @Schema(description = "物流公司")
    private String logisticsCompany;

    @Schema(description = "快递单号")
    private String trackingNumber;

    @Schema(description = "发货人")
    private String sender;

    @Schema(description = "发货人电话")
    private String senderPhone;

    @Schema(description = "发货地址")
    private String senderAddress;

    @Schema(description = "收货人")
    @NotBlank(message = "收货人不能为空")
    private String receiver;

    @Schema(description = "收货人电话")
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    @Schema(description = "收货地址")
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    @Schema(description = "发货日期")
    private LocalDate shipmentDate;

    @Schema(description = "预计到达日期")
    private LocalDate estimatedArrivalDate;

    @Schema(description = "实际到达日期")
    private LocalDate actualArrivalDate;

    @Schema(description = "物流费用")
    private BigDecimal logisticsCost;

    @Schema(description = "物流状态")
    private String logisticsStatus;

    @Schema(description = "备注")
    private String remark;
}
