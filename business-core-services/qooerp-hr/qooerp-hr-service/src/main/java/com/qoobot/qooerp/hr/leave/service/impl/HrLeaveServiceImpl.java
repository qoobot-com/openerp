package com.qoobot.qooerp.hr.leave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.leave.domain.HrLeave;
import com.qoobot.qooerp.hr.leave.mapper.HrLeaveMapper;
import com.qoobot.qooerp.hr.leave.service.IHrLeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 请假申请服务实现
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrLeaveServiceImpl extends ServiceImpl<HrLeaveMapper, HrLeave>
        implements IHrLeaveService {

    @Override
    public Long submitLeave(HrLeave leave) {
        log.info("提交请假申请: 申请人={}, 开始日期={}, 结束日期={}",
                leave.getApplicantName(), leave.getStartDate(), leave.getEndDate());

        // 计算请假天数
        BigDecimal leaveDays = calculateLeaveDays(
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getStartTime(),
                leave.getEndTime()
        );
        leave.setLeaveDays(leaveDays);

        // 设置状态为待审批
        leave.setApprovalStatus(0);

        this.save(leave);
        return leave.getId();
    }

    @Override
    public Boolean approveLeave(Long leaveId, Long approverId, Boolean approved, String comment) {
        log.info("审批请假申请: 请假ID={}, 审批人={}, 是否通过={}", leaveId, approverId, approved);

        HrLeave leave = this.getById(leaveId);
        if (leave == null) {
            log.error("请假申请不存在: {}", leaveId);
            return false;
        }

        // 更新审批信息
        leave.setApproverId(approverId);
        leave.setApprovalStatus(approved ? 1 : 2);
        leave.setApprovalTime(LocalDateTime.now());
        leave.setApprovalComment(comment);

        return this.updateById(leave);
    }

    @Override
    public Boolean cancelLeave(Long leaveId) {
        log.info("撤销请假申请: {}", leaveId);

        HrLeave leave = this.getById(leaveId);
        if (leave == null) {
            log.error("请假申请不存在: {}", leaveId);
            return false;
        }

        // 只能撤销待审批的申请
        if (leave.getApprovalStatus() != 0) {
            log.error("只能撤销待审批的申请: 当前状态={}", leave.getApprovalStatus());
            return false;
        }

        leave.setApprovalStatus(3);
        return this.updateById(leave);
    }

    @Override
    public List<HrLeave> getMyLeave(Long employeeId) {
        LambdaQueryWrapper<HrLeave> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrLeave::getApplicantId, employeeId);
        wrapper.orderByDesc(HrLeave::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HrLeave> getPendingApproval(Long approverId) {
        LambdaQueryWrapper<HrLeave> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrLeave::getApprovalStatus, 0);
        // TODO: 根据审批人权限过滤
        wrapper.orderByAsc(HrLeave::getStartDate);
        return this.list(wrapper);
    }

    @Override
    public BigDecimal calculateLeaveDays(LocalDate startDate, LocalDate endDate, String startTime, String endTime) {
        List<LocalDate> workDays = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            // 排除周末（周六、周日）
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workDays.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        int totalDays = workDays.size();

        // 如果指定了时间，计算半天
        if (startTime != null && !startTime.isEmpty()) {
            return new BigDecimal(totalDays - 0.5);
        } else if (endTime != null && !endTime.isEmpty()) {
            return new BigDecimal(totalDays - 0.5);
        }

        return new BigDecimal(totalDays);
    }
}
