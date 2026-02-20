package com.qoobot.qooerp.scm.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 客户查询DTO
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@Schema(description = "客户查询DTO")
public class CustomerQueryDTO {

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "客户类型")
    private String customerType;

    @Schema(description = "客户等级")
    private String customerLevel;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "页码")
    private Integer current = 1;

    @Schema(description = "每页条数")
    private Integer size = 10;
}
