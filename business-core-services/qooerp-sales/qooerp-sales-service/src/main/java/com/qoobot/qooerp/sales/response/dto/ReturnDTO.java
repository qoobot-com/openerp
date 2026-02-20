package com.qoobot.qooerp.sales.response.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售退货 DTO
 */
@Data
@Schema(description = "销售退货DTO")
public class ReturnDTO {

    @Schema(description = "退货单ID")
    private Long id;

    @Schema(description = "退货单号")
    private String returnNo;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "发货单ID")
    private Long deliveryId;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "退货日期")
    private String returnDate;

    @Schema(description = "退货原因")
    private String returnReason;

    @Schema(description = "退货类型")
    private String returnType;

    @Schema(description = "退货状态")
    private String status;

    @Schema(description = "退货总金额")
    private BigDecimal totalAmount;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "审核人ID")
    private Long approverId;

    @Schema(description = "审核人姓名")
    private String approverName;

    @Schema(description = "审核时间")
    private LocalDateTime approveTime;

    @Schema(description = "审核意见")
    private String approveRemark;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "退货明细")
    private List<ReturnDetailDTO> details;
}
