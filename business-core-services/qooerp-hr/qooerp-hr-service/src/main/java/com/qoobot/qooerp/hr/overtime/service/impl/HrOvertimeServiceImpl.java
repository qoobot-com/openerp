package com.qoobot.qooerp.hr.overtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.overtime.domain.HrOvertime;
import com.qoobot.qooerp.hr.overtime.mapper.HrOvertimeMapper;
import com.qoobot.qooerp.hr.overtime.service.IHrOvertimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 加班申请服务实现
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrOvertimeServiceImpl extends ServiceImpl<HrOvertimeMapper, HrOvertime>
        implements IHrOvertimeService {

    @Override
    public Long submitOvertime(HrOvertime overtime) {
        log.info("提交加班申请: 申请人={}, 开始时间={}, 结束时间={}",
                overtime.getApplicantName(), overtime.getStartTime(), overtime.getEndTime());

        // 计算加班时长
        if (overtime.getStartTime() != null && overtime.getEndTime() != null) {
            long minutes = Duration.between(overtime.getStartTime(), overtime.getEndTime()).toMinutes();
            overtime.setDuration(minutes / 60.0);
        }

        // 设置状态为待审批
        overtime.setApprovalStatus(0);
        overtime.setLeaveStatus(0);

        this.save(overtime);
        return overtime.getId();
    }

    @Override
    public Boolean approveOvertime(Long overtimeId, Long approverId, Boolean approved, String comment) {
        log.info("审批加班申请: 加班ID={}, 审批人={}, 是否通过={}", overtimeId, approverId, approved);

        HrOvertime overtime = this.getById(overtimeId);
        if (overtime == null) {
            log.error("加班申请不存在: {}", overtimeId);
            return false;
        }

        // 更新审批信息
        overtime.setApproverId(approverId);
        overtime.setApprovalStatus(approved ? 1 : 2);
        overtime.setApprovalTime(LocalDateTime.now());
        overtime.setApprovalComment(comment);

        return this.updateById(overtime);
    }

    @Override
    public Boolean cancelOvertime(Long overtimeId) {
        log.info("撤销加班申请: {}", overtimeId);

        HrOvertime overtime = this.getById(overtimeId);
        if (overtime == null) {
            log.error("加班申请不存在: {}", overtimeId);
            return false;
        }

        // 只能撤销待审批的申请
        if (overtime.getApprovalStatus() != 0) {
            log.error("只能撤销待审批的申请: 当前状态={}", overtime.getApprovalStatus());
            return false;
        }

        overtime.setApprovalStatus(3);
        return this.updateById(overtime);
    }

    @Override
    public List<HrOvertime> getMyOvertime(Long employeeId) {
        LambdaQueryWrapper<HrOvertime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrOvertime::getApplicantId, employeeId);
        wrapper.orderByDesc(HrOvertime::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HrOvertime> getPendingApproval(Long approverId) {
        LambdaQueryWrapper<HrOvertime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrOvertime::getApprovalStatus, 0);
        // TODO: 根据审批人权限过滤
        wrapper.orderByAsc(HrOvertime::getStartTime);
        return this.list(wrapper);
    }

    @Override
    public Boolean useCompensatoryLeave(Long overtimeId) {
        log.info("使用加班调休: {}", overtimeId);

        HrOvertime overtime = this.getById(overtimeId);
        if (overtime == null) {
            log.error("加班申请不存在: {}", overtimeId);
            return false;
        }

        // 只能使用已通过的加班
        if (overtime.getApprovalStatus() != 1) {
            log.error("只能使用已通过的加班调休: 当前状态={}", overtime.getApprovalStatus());
            return false;
        }

        // 只能使用未使用的加班
        if (overtime.getLeaveStatus() != 0) {
            log.error("加班已使用或已过期: 当前状态={}", overtime.getLeaveStatus());
            return false;
        }

        overtime.setLeaveStatus(1);
        return this.updateById(overtime);
    }
}
