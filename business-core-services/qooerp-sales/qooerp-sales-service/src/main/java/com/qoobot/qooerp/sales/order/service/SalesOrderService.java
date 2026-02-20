package com.qoobot.qooerp.sales.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.sales.order.domain.SalesOrder;
import com.qoobot.qooerp.sales.order.dto.OrderDTO;
import com.qoobot.qooerp.sales.order.dto.OrderQueryDTO;

/**
 * 销售订单服务接口
 */
public interface SalesOrderService extends IService<SalesOrder> {

    /**
     * 创建订单
     */
    Result<Long> createOrder(OrderDTO orderDTO);

    /**
     * 审核订单
     */
    Result<Void> approveOrder(Long orderId, Long approverId);

    /**
     * 取消订单
     */
    Result<Void> cancelOrder(Long orderId, String reason);

    /**
     * 订单发货
     */
    Result<Void> shipOrder(Long orderId);

    /**
     * 查询订单
     */
    Result<OrderDTO> getOrder(Long orderId);

    /**
     * 分页查询订单
     */
    Result<PageResult<OrderDTO>> queryOrders(OrderQueryDTO queryDTO);
}
