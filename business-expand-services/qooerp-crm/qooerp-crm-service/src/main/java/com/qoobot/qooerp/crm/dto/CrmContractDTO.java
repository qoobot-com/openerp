package com.qoobot.qooerp.crm.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同DTO
 */
@Data
public class CrmContractDTO implements Serializable {

    private Long id;

    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    private Long opportunityId;

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    private String customerName;

    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    @NotNull(message = "合同金额不能为空")
    private BigDecimal contractAmount;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    private Integer status;
}
