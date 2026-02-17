package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.dto.ProductionOrderDTO;
import com.qoobot.qooerp.production.entity.ProductionOrder;
import com.qoobot.qooerp.production.vo.ProductionOrderVO;

import java.util.List;

/**
 * 生产订单Service接口
 */
public interface ProductionOrderService extends IService<ProductionOrder> {

    /**
     * 创建生产订单
     *
     * @param dto 生产订单DTO
     * @return 生产订单ID
     */
    Long createProductionOrder(ProductionOrderDTO dto);

    /**
     * 更新生产订单
     *
     * @param id 生产订单ID
     * @param dto 生产订单DTO
     * @return 是否成功
     */
    Boolean updateProductionOrder(Long id, ProductionOrderDTO dto);

    /**
     * 删除生产订单
     *
     * @param id 生产订单ID
     * @return 是否成功
     */
    Boolean deleteProductionOrder(Long id);

    /**
     * 根据ID查询生产订单
     *
     * @param id 生产订单ID
     * @return 生产订单VO
     */
    ProductionOrderVO getProductionOrderById(Long id);

    /**
     * 分页查询生产订单
     *
     * @param current 当前页
     * @param size 每页大小
     * @param orderNo 订单编号
     * @param status 状态
     * @param workshopId 车间ID
     * @return 分页结果
     */
    IPage<ProductionOrderVO> queryProductionOrderPage(Integer current, Integer size, String orderNo, String status, Long workshopId);

    /**
     * 批量删除生产订单
     *
     * @param ids 生产订单ID列表
     * @return 是否成功
     */
    Boolean batchDeleteProductionOrder(List<Long> ids);

    /**
     * 开始生产
     *
     * @param id 生产订单ID
     * @return 是否成功
     */
    Boolean startProduction(Long id);

    /**
     * 完成生产
     *
     * @param id 生产订单ID
     * @return 是否成功
     */
    Boolean completeProduction(Long id);

    /**
     * 取消生产订单
     *
     * @param id 生产订单ID
     * @return 是否成功
     */
    Boolean cancelProductionOrder(Long id);

    /**
     * 更新订单进度
     *
     * @param id 生产订单ID
     * @param completedQuantity 已完成数量
     * @param qualifiedQuantity 合格数量
     * @param rejectQuantity 不良数量
     * @return 是否成功
     */
    Boolean updateProgress(Long id, java.math.BigDecimal completedQuantity,
                           java.math.BigDecimal qualifiedQuantity, java.math.BigDecimal rejectQuantity);
}
