package com.qoobot.qooerp.hr.overtime.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.overtime.domain.HrOvertime;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 加班申请服务接口
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
public interface IHrOvertimeService extends IService<HrOvertime> {

    /**
     * 提交加班申请
     *
     * @param overtime 加班信息
     * @return 加班ID
     */
    Long submitOvertime(HrOvertime overtime);

    /**
     * 审批加班申请
     *
     * @param overtimeId 加班ID
     * @param approverId 审批人ID
     * @param approved 是否通过
     * @param comment 审批意见
     * @return 是否成功
     */
    Boolean approveOvertime(Long overtimeId, Long approverId, Boolean approved, String comment);

    /**
     * 撤销加班申请
     *
     * @param overtimeId 加班ID
     * @return 是否成功
     */
    Boolean cancelOvertime(Long overtimeId);

    /**
     * 获取我的加班申请
     *
     * @param employeeId 员工ID
     * @return 加班申请列表
     */
    List<HrOvertime> getMyOvertime(Long employeeId);

    /**
     * 获取待审批的加班申请
     *
     * @param approverId 审批人ID
     * @return 加班申请列表
     */
    List<HrOvertime> getPendingApproval(Long approverId);

    /**
     * 使用加班调休
     *
     * @param overtimeId 加班ID
     * @return 是否成功
     */
    Boolean useCompensatoryLeave(Long overtimeId);
}
