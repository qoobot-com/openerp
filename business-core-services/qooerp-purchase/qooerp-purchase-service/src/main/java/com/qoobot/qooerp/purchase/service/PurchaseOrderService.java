package com.qoobot.qooerp.purchase.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.purchase.dto.OrderCreateDTO;
import com.qoobot.qooerp.purchase.dto.OrderDTO;
import com.qoobot.qooerp.purchase.dto.OrderQueryDTO;
import com.qoobot.qooerp.purchase.dto.OrderUpdateDTO;
import com.qoobot.qooerp.purchase.dto.RejectReasonDTO;

public interface PurchaseOrderService {

    Result<Long> createOrder(OrderCreateDTO dto);

    Result<Void> updateOrder(OrderUpdateDTO dto);

    Result<Void> deleteOrder(Long id);

    Result<Void> submitOrder(Long id);

    Result<Void> approveOrder(Long id);

    Result<Void> rejectOrder(Long id, RejectReasonDTO dto);

    Result<Void> cancelOrder(Long id);

    Result<Void> completeOrder(Long id);

    Result<OrderDTO> getOrder(Long id);

    Result<PageResult<OrderDTO>> queryPage(OrderQueryDTO dto);
}
