package com.qoobot.qooerp.finance.arap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 收款记录DTO
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@Schema(description = "收款记录DTO")
public class ReceiptDTO {

    @Schema(description = "收款记录ID")
    private Long id;

    @Schema(description = "收款单号")
    private String receiptCode;

    @Schema(description = "应收单ID")
    private Long receivableId;

    @Schema(description = "应收单号")
    private String receivableCode;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "收款金额")
    private BigDecimal receiptAmount;

    @Schema(description = "收款方式")
    private String paymentMethod;

    @Schema(description = "银行账户")
    private String bankAccount;

    @Schema(description = "收款日期")
    private LocalDate receiptDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private String createdBy;

    @Schema(description = "创建时间")
    private String createdTime;
}
