package com.qoobot.qooerp.hr.leave.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.leave.domain.HrLeave;

import java.util.List;

/**
 * 请假申请服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IHrLeaveService extends IService<HrLeave> {

    /**
     * 提交请假申请
     *
     * @param leave 请假信息
     * @return 请假ID
     */
    Long submitLeave(HrLeave leave);

    /**
     * 审批请假申请
     *
     * @param leaveId 请假ID
     * @param approverId 审批人ID
     * @param approved 是否通过
     * @param comment 审批意见
     * @return 是否成功
     */
    Boolean approveLeave(Long leaveId, Long approverId, Boolean approved, String comment);

    /**
     * 撤销请假申请
     *
     * @param leaveId 请假ID
     * @return 是否成功
     */
    Boolean cancelLeave(Long leaveId);

    /**
     * 获取我的请假申请
     *
     * @param employeeId 员工ID
     * @return 请假申请列表
     */
    List<HrLeave> getMyLeave(Long employeeId);

    /**
     * 获取待审批的请假申请
     *
     * @param approverId 审批人ID
     * @return 请假申请列表
     */
    List<HrLeave> getPendingApproval(Long approverId);

    /**
     * 计算请假天数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param startTime 开始时间（空表示全天）
     * @param endTime 结束时间（空表示全天）
     * @return 请假天数
     */
    java.math.BigDecimal calculateLeaveDays(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            String startTime,
            String endTime);
}
