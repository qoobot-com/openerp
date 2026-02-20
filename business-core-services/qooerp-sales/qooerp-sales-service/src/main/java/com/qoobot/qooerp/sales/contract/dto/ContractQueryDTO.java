package com.qoobot.qooerp.sales.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 销售合同查询 DTO
 */
@Data
@Schema(description = "销售合同查询DTO")
public class ContractQueryDTO {

    @Schema(description = "合同编号")
    private String contractNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "合同状态")
    private String status;

    @Schema(description = "合同类型")
    private String contractType;

    @Schema(description = "开始日期")
    private String startDate;

    @Schema(description = "结束日期")
    private String endDate;

    @Schema(description = "当前页码")
    private Integer current = 1;

    @Schema(description = "每页条数")
    private Integer size = 10;
}
