package com.qoobot.qooerp.scm.logistics.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.logistics.domain.LogisticsTracking;
import com.qoobot.qooerp.scm.logistics.dto.LogisticsTrackingDTO;
import com.qoobot.qooerp.scm.logistics.dto.TrackingQueryDTO;

/**
 * 物流跟踪Service接口
 *
 * @author QooERP
 * @since 2026-02-17
 */
public interface ILogisticsTrackingService {

    /**
     * 创建物流跟踪
     *
     * @param dto 物流跟踪DTO
     * @return 跟踪ID
     */
    Long create(LogisticsTrackingDTO dto);

    /**
     * 更新物流跟踪
     *
     * @param dto 物流跟踪DTO
     * @return 是否成功
     */
    boolean update(LogisticsTrackingDTO dto);

    /**
     * 删除物流跟踪
     *
     * @param id 跟踪ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 获取物流跟踪详情
     *
     * @param id 跟踪ID
     * @return 物流跟踪DTO
     */
    LogisticsTrackingDTO getDetail(Long id);

    /**
     * 分页查询物流跟踪列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<LogisticsTracking> queryPage(TrackingQueryDTO queryDTO);

    /**
     * 根据运单号查询物流跟踪
     *
     * @param trackingNumber 运单号
     * @return 物流跟踪DTO
     */
    LogisticsTrackingDTO getByTrackingNumber(String trackingNumber);

    /**
     * 根据订单ID查询物流跟踪
     *
     * @param orderId 订单ID
     * @return 物流跟踪DTO
     */
    LogisticsTrackingDTO getByOrderId(Long orderId);

    /**
     * 更新物流状态
     *
     * @param id 跟踪ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, String status);

    /**
     * 生成运单号
     *
     * @return 运单号
     */
    String generateTrackingNumber();

    /**
     * 校验运单号是否存在
     *
     * @param trackingNumber 运单号
     * @param excludeId 排除的ID（更新时使用）
     * @return 是否存在
     */
    boolean checkTrackingNumberExists(String trackingNumber, Long excludeId);

    /**
     * 更新物流位置
     *
     * @param id 跟踪ID
     * @param currentLocation 当前位置
     * @return 是否成功
     */
    boolean updateLocation(Long id, String currentLocation);
}
