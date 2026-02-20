package com.qoobot.qooerp.crm.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 跟进记录DTO
 */
@Data
public class CrmFollowupDTO implements Serializable {

    private Long id;

    @NotBlank(message = "跟进编号不能为空")
    private String followupNo;

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    private Long opportunityId;

    @NotBlank(message = "跟进类型不能为空")
    private String followupType;

    @NotBlank(message = "跟进内容不能为空")
    private String followupContent;

    @NotNull(message = "跟进日期不能为空")
    private LocalDate followupDate;

    @NotNull(message = "跟进人ID不能为空")
    private Long followupPersonId;

    private String followupPersonName;
}
