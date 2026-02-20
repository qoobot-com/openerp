package com.qoobot.qooerp.sales.receipt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.sales.receipt.dto.ReceiptDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售收款 Service 接口
 */
public interface SalesReceiptService {

    /**
     * 创建收款单
     */
    ReceiptDTO createReceipt(ReceiptDTO dto);

    /**
     * 更新收款单
     */
    ReceiptDTO updateReceipt(Long id, ReceiptDTO dto);

    /**
     * 删除收款单
     */
    void deleteReceipt(Long id);

    /**
     * 根据ID查询收款单
     */
    ReceiptDTO getReceiptById(Long id);

    /**
     * 分页查询收款单
     */
    Page<ReceiptDTO> queryReceipts(Long customerId, String status, Integer current, Integer size);

    /**
     * 根据订单ID查询收款单
     */
    List<ReceiptDTO> getReceiptsByOrderId(Long orderId);

    /**
     * 确认收款
     */
    void confirmReceipt(Long id, Long receiverId);

    /**
     * 核销应收账款
     */
    void writeoffReceipt(Long id, Long orderId, BigDecimal amount);

    /**
     * 生成收款单号
     */
    String generateReceiptNo();
}
