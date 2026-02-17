package com.qoobot.qooerp.scm.logistics.cost.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.logistics.cost.domain.LogisticsCost;
import com.qoobot.qooerp.scm.logistics.cost.dto.LogisticsCostDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物流费用Service接口
 *
 * @author QooERP
 * @since 2026-02-17
 */
public interface ILogisticsCostService {

    /**
     * 创建物流费用
     *
     * @param dto 物流费用DTO
     * @return 费用ID
     */
    Long create(LogisticsCostDTO dto);

    /**
     * 更新物流费用
     *
     * @param dto 物流费用DTO
     * @return 是否成功
     */
    boolean update(LogisticsCostDTO dto);

    /**
     * 删除物流费用
     *
     * @param id 费用ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 获取费用详情
     *
     * @param id 费用ID
     * @return 费用DTO
     */
    LogisticsCostDTO getDetail(Long id);

    /**
     * 分页查询费用列表
     *
     * @param trackingId 物流跟踪ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<LogisticsCost> queryPage(Long trackingId, Integer pageNo, Integer pageSize);

    /**
     * 根据物流跟踪ID获取费用列表
     *
     * @param trackingId 物流跟踪ID
     * @return 费用列表
     */
    List<LogisticsCost> getCostsByTrackingId(Long trackingId);

    /**
     * 根据订单ID获取费用列表
     *
     * @param orderId 订单ID
     * @return 费用列表
     */
    List<LogisticsCost> getCostsByOrderId(Long orderId);

    /**
     * 计算物流跟踪总费用
     *
     * @param trackingId 物流跟踪ID
     * @return 总费用
     */
    BigDecimal calculateTotalCost(Long trackingId);

    /**
     * 更新付款状态
     *
     * @param id 费用ID
     * @param paidStatus 付款状态
     * @param paidAmount 已付金额
     * @param paidDate 付款日期
     * @return 是否成功
     */
    boolean updatePaymentStatus(Long id, String paidStatus, BigDecimal paidAmount, java.time.LocalDate paidDate);

    /**
     * 获取费用统计
     *
     * @param trackingId 物流跟踪ID
     * @return 统计信息（总费用、已付、未付）
     */
    java.util.Map<String, BigDecimal> getCostStatistics(Long trackingId);
}
