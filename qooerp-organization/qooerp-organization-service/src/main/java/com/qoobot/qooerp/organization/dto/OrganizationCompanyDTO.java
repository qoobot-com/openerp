package com.qoobot.qooerp.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 公司DTO
 */
@Data
@Schema(description = "公司DTO")
public class OrganizationCompanyDTO {

    @Schema(description = "公司ID")
    private Long id;

    @Schema(description = "公司编码")
    @NotBlank(message = "公司编码不能为空")
    private String companyCode;

    @Schema(description = "公司名称")
    @NotBlank(message = "公司名称不能为空")
    private String companyName;

    @Schema(description = "英文名称")
    private String companyNameEn;

    @Schema(description = "父公司ID")
    private Long parentId;

    @Schema(description = "法人代表")
    private String legalPerson;

    @Schema(description = "统一社会信用代码")
    private String creditCode;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "公司Logo")
    private String logo;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
