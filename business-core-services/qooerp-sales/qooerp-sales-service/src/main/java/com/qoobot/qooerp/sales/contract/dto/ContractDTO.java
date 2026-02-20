package com.qoobot.qooerp.sales.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售合同 DTO
 */
@Data
@Schema(description = "销售合同DTO")
public class ContractDTO {

    @Schema(description = "合同ID")
    private Long id;

    @Schema(description = "合同编号")
    private String contractNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "订单ID（关联）")
    private Long orderId;

    @Schema(description = "签订日期")
    private LocalDate signDate;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "合同金额")
    private BigDecimal contractAmount;

    @Schema(description = "已支付金额")
    private BigDecimal paidAmount;

    @Schema(description = "付款方式")
    private String paymentMethod;

    @Schema(description = "付款条款")
    private String paymentTerms;

    @Schema(description = "交货方式")
    private String deliveryMethod;

    @Schema(description = "合同状态")
    private String status;

    @Schema(description = "合同类型")
    private String contractType;

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

    @Schema(description = "合同明细")
    private List<ContractDetailDTO> details;
}
