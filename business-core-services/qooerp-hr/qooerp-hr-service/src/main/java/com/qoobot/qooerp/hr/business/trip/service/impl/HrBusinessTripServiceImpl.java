package com.qoobot.qooerp.hr.business.trip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.business.trip.domain.HrBusinessTrip;
import com.qoobot.qooerp.hr.business.trip.mapper.HrBusinessTripMapper;
import com.qoobot.qooerp.hr.business.trip.service.IHrBusinessTripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 出差申请服务实现
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrBusinessTripServiceImpl extends ServiceImpl<HrBusinessTripMapper, HrBusinessTrip>
        implements IHrBusinessTripService {

    @Override
    public Long submitBusinessTrip(HrBusinessTrip trip) {
        log.info("提交出差申请: 申请人={}, 目的地={}, 开始日期={}, 结束日期={}",
                trip.getApplicantName(), trip.getDestination(), trip.getStartDate(), trip.getEndDate());

        // 计算出差天数
        if (trip.getStartDate() != null && trip.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;
            trip.setDays((int) days);
        }

        // 计算总预算
        BigDecimal totalBudget = BigDecimal.ZERO;
        if (trip.getTransportBudget() != null) {
            totalBudget = totalBudget.add(trip.getTransportBudget());
        }
        if (trip.getAccommodationBudget() != null) {
            totalBudget = totalBudget.add(trip.getAccommodationBudget());
        }
        if (trip.getMealBudget() != null) {
            totalBudget = totalBudget.add(trip.getMealBudget());
        }
        if (trip.getOtherBudget() != null) {
            totalBudget = totalBudget.add(trip.getOtherBudget());
        }
        trip.setTotalBudget(totalBudget);

        // 设置状态为待审批
        trip.setApprovalStatus(0);

        this.save(trip);
        return trip.getId();
    }

    @Override
    public Boolean approveBusinessTrip(Long tripId, Long approverId, Boolean approved, String comment) {
        log.info("审批出差申请: 出差ID={}, 审批人={}, 是否通过={}", tripId, approverId, approved);

        HrBusinessTrip trip = this.getById(tripId);
        if (trip == null) {
            log.error("出差申请不存在: {}", tripId);
            return false;
        }

        // 更新审批信息
        trip.setApproverId(approverId);
        trip.setApprovalStatus(approved ? 1 : 2);
        trip.setApprovalTime(LocalDateTime.now());
        trip.setApprovalComment(comment);

        return this.updateById(trip);
    }

    @Override
    public Boolean cancelBusinessTrip(Long tripId) {
        log.info("撤销出差申请: {}", tripId);

        HrBusinessTrip trip = this.getById(tripId);
        if (trip == null) {
            log.error("出差申请不存在: {}", tripId);
            return false;
        }

        // 只能撤销待审批的申请
        if (trip.getApprovalStatus() != 0) {
            log.error("只能撤销待审批的申请: 当前状态={}", trip.getApprovalStatus());
            return false;
        }

        trip.setApprovalStatus(3);
        return this.updateById(trip);
    }

    @Override
    public List<HrBusinessTrip> getMyBusinessTrips(Long employeeId) {
        LambdaQueryWrapper<HrBusinessTrip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrBusinessTrip::getApplicantId, employeeId);
        wrapper.orderByDesc(HrBusinessTrip::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HrBusinessTrip> getPendingApproval(Long approverId) {
        LambdaQueryWrapper<HrBusinessTrip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrBusinessTrip::getApprovalStatus, 0);
        // TODO: 根据审批人权限过滤
        wrapper.orderByAsc(HrBusinessTrip::getStartDate);
        return this.list(wrapper);
    }
}
