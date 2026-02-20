package com.qoobot.qooerp.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公司VO
 */
@Data
@Schema(description = "公司VO")
public class OrganizationCompanyVO {

    @Schema(description = "公司ID")
    private Long id;

    @Schema(description = "公司编码")
    private String companyCode;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "英文名称")
    private String companyNameEn;

    @Schema(description = "父公司ID")
    private Long parentId;

    @Schema(description = "公司级别")
    private Integer companyLevel;

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
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updateBy;
}
