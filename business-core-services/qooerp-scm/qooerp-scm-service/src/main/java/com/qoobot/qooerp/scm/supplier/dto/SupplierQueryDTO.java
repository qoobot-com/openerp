package com.qoobot.qooerp.scm.supplier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 供应商查询DTO
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@Schema(description = "供应商查询DTO")
public class SupplierQueryDTO {

    @Schema(description = "供应商编码")
    private String supplierCode;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "供应商类型")
    private String supplierType;

    @Schema(description = "信用等级")
    private String creditLevel;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "页码")
    private Integer current = 1;

    @Schema(description = "每页条数")
    private Integer size = 10;
}
