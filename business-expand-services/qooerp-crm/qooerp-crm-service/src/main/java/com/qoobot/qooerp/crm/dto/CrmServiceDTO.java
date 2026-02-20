package com.qoobot.qooerp.crm.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 客户服务DTO
 */
@Data
public class CrmServiceDTO implements Serializable {

    private Long id;

    @NotBlank(message = "服务编号不能为空")
    private String serviceNo;

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    private String customerName;

    @NotBlank(message = "服务类型不能为空")
    private String serviceType;

    @NotBlank(message = "服务标题不能为空")
    private String serviceTitle;

    private String description;

    @NotNull(message = "优先级不能为空")
    private Integer priority;

    private Integer status;

    private Long assigneeId;

    private String assigneeName;
}
