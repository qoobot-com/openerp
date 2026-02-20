package com.qoobot.qooerp.scm.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户DTO
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@Schema(description = "客户DTO")
public class CustomerDTO {

    @Schema(description = "客户ID")
    private Long id;

    @Schema(description = "客户名称")
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @Schema(description = "客户类型")
    private String customerType;

    @Schema(description = "所属行业")
    private String industry;

    @Schema(description = "企业规模")
    private String scale;

    @Schema(description = "客户等级")
    private String customerLevel;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "联系邮箱")
    private String contactEmail;

    @Schema(description = "联系地址")
    private String address;

    @Schema(description = "税号")
    private String taxNumber;

    @Schema(description = "开户银行")
    private String bankName;

    @Schema(description = "银行账号")
    private String bankAccount;

    @Schema(description = "信用额度")
    private BigDecimal creditLimit;

    @Schema(description = "结算方式")
    private String paymentMethod;

    @Schema(description = "付款期限（天）")
    private Integer paymentDays;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;
}
