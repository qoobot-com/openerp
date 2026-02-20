package com.qoobot.qooerp.finance.invoice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 发票查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "发票查询DTO")
public class InvoiceQueryDTO {

    @Schema(description = "发票编号（模糊查询）")
    private String invoiceNo;

    @Schema(description = "发票类型")
    private String invoiceType;

    @Schema(description = "发票种类")
    private String invoiceCategory;

    @Schema(description = "开票开始日期")
    private LocalDate startDate;

    @Schema(description = "开票结束日期")
    private LocalDate endDate;

    @Schema(description = "发票状态")
    private String status;

    @Schema(description = "购买方名称（模糊查询）")
    private String buyerName;

    @Schema(description = "销售方名称（模糊查询）")
    private String sellerName;

    @Schema(description = "页码")
    private Integer pageNo = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}
