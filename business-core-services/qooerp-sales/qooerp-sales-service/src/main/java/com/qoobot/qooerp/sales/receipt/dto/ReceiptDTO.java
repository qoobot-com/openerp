package com.qoobot.qooerp.sales.receipt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售收款 DTO
 */
@Data
@Schema(description = "销售收款DTO")
public class ReceiptDTO {

    @Schema(description = "收款单ID")
    private Long id;

    @Schema(description = "收款单号")
    private String receiptNo;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "收款日期")
    private String receiptDate;

    @Schema(description = "收款金额")
    private BigDecimal receiptAmount;

    @Schema(description = "收款方式")
    private String paymentMethod;

    @Schema(description = "收款状态")
    private String status;

    @Schema(description = "核销订单ID")
    private Long writeoffOrderId;

    @Schema(description = "核销金额")
    private BigDecimal writeoffAmount;

    @Schema(description = "收款人ID")
    private Long receiverId;

    @Schema(description = "收款人姓名")
    private String receiverName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "收款明细")
    private List<ReceiptDetailDTO> details;
}
