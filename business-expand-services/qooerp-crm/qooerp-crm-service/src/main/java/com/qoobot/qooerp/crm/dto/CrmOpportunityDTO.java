package com.qoobot.qooerp.crm.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商机DTO
 */
@Data
public class CrmOpportunityDTO implements Serializable {

    private Long id;

    @NotBlank(message = "商机编号不能为空")
    private String opportunityNo;

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    private String customerName;

    @NotBlank(message = "商机名称不能为空")
    private String opportunityName;

    @NotNull(message = "商机阶段不能为空")
    private Integer stage;

    private BigDecimal amount;

    private Integer probability;

    private LocalDate expectedCloseDate;

    @NotNull(message = "负责人ID不能为空")
    private Long ownerId;

    private String ownerName;

    private Integer status;
}
