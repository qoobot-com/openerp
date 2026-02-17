package com.qoobot.qooerp.hr.business.trip.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.business.trip.domain.HrBusinessTrip;

import java.util.List;

/**
 * 出差申请服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IHrBusinessTripService extends IService<HrBusinessTrip> {

    /**
     * 提交出差申请
     *
     * @param trip 出差信息
     * @return 出差ID
     */
    Long submitBusinessTrip(HrBusinessTrip trip);

    /**
     * 审批出差申请
     *
     * @param tripId 出差ID
     * @param approverId 审批人ID
     * @param approved 是否通过
     * @param comment 审批意见
     * @return 是否成功
     */
    Boolean approveBusinessTrip(Long tripId, Long approverId, Boolean approved, String comment);

    /**
     * 撤销出差申请
     *
     * @param tripId 出差ID
     * @return 是否成功
     */
    Boolean cancelBusinessTrip(Long tripId);

    /**
     * 获取我的出差申请
     *
     * @param employeeId 员工ID
     * @return 出差申请列表
     */
    List<HrBusinessTrip> getMyBusinessTrips(Long employeeId);

    /**
     * 获取待审批的出差申请
     *
     * @param approverId 审批人ID
     * @return 出差申请列表
     */
    List<HrBusinessTrip> getPendingApproval(Long approverId);
}
