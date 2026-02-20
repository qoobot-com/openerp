package com.qoobot.qooerp.sales.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售发货 DTO
 */
@Data
@Schema(description = "销售发货DTO")
public class DeliveryDTO {

    @Schema(description = "发货单ID")
    private Long id;

    @Schema(description = "发货单号")
    private String deliveryNo;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "收货地址")
    private String address;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "发货日期")
    private String deliveryDate;

    @Schema(description = "物流公司")
    private String logisticsCompany;

    @Schema(description = "物流单号")
    private String trackingNo;

    @Schema(description = "发货状态")
    private String status;

    @Schema(description = "签收人")
    private String receiver;

    @Schema(description = "签收时间")
    private LocalDateTime receiveTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "发货明细")
    private List<DeliveryDetailDTO> details;
}
