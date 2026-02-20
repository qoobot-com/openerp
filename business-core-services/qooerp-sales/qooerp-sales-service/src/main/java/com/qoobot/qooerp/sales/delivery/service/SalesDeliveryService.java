package com.qoobot.qooerp.sales.delivery.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.sales.delivery.dto.DeliveryDTO;

import java.util.List;

/**
 * 销售发货 Service 接口
 */
public interface SalesDeliveryService {

    /**
     * 创建发货单
     */
    DeliveryDTO createDelivery(DeliveryDTO dto);

    /**
     * 更新发货单
     */
    DeliveryDTO updateDelivery(Long id, DeliveryDTO dto);

    /**
     * 删除发货单
     */
    void deleteDelivery(Long id);

    /**
     * 根据ID查询发货单
     */
    DeliveryDTO getDeliveryById(Long id);

    /**
     * 分页查询发货单
     */
    Page<DeliveryDTO> queryDeliveries(Long orderId, Integer current, Integer size);

    /**
     * 根据订单ID查询发货单
     */
    List<DeliveryDTO> getDeliveriesByOrderId(Long orderId);

    /**
     * 确认发货
     */
    void confirmDelivery(Long id);

    /**
     * 确认签收
     */
    void confirmReceive(Long id, String receiver);

    /**
     * 生成发货单号
     */
    String generateDeliveryNo();
}
