package com.qoobot.qooerp.sales.response.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.sales.response.dto.ReturnDTO;

/**
 * 销售退货 Service 接口
 */
public interface SalesReturnService {

    /**
     * 创建退货单
     */
    ReturnDTO createReturn(ReturnDTO dto);

    /**
     * 更新退货单
     */
    ReturnDTO updateReturn(Long id, ReturnDTO dto);

    /**
     * 删除退货单
     */
    void deleteReturn(Long id);

    /**
     * 根据ID查询退货单
     */
    ReturnDTO getReturnById(Long id);

    /**
     * 分页查询退货单
     */
    Page<ReturnDTO> queryReturns(Long orderId, String status, Integer current, Integer size);

    /**
     * 审核退货单
     */
    void approveReturn(Long id, Long approverId, String approveRemark, boolean approved);

    /**
     * 退货处理
     */
    void processReturn(Long id);

    /**
     * 生成退货单号
     */
    String generateReturnNo();
}
