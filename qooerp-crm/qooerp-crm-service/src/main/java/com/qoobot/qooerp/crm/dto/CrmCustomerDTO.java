package com.qoobot.crm.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 客户DTO
 */
@Data
public class CrmCustomerDTO implements Serializable {

    private Long id;

    @NotBlank(message = "客户编号不能为空")
    private String customerNo;

    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @NotNull(message = "客户类型不能为空")
    private Integer customerType;

    private Integer level;

    private String industry;

    private String region;

    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String contactEmail;

    private String address;

    private Integer status;
}
