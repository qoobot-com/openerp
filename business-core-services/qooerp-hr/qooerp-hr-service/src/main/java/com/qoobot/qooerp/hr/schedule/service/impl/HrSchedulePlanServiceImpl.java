package com.qoobot.qooerp.hr.schedule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.schedule.domain.HrSchedulePlan;
import com.qoobot.qooerp.hr.schedule.domain.HrScheduleRule;
import com.qoobot.qooerp.hr.schedule.domain.HrShift;
import com.qoobot.qooerp.hr.schedule.mapper.HrSchedulePlanMapper;
import com.qoobot.qooerp.hr.schedule.service.IHrSchedulePlanService;
import com.qoobot.qooerp.hr.schedule.service.IHrScheduleRuleService;
import com.qoobot.qooerp.hr.schedule.service.IHrShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排班计划服务实现
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrSchedulePlanServiceImpl extends ServiceImpl<HrSchedulePlanMapper, HrSchedulePlan>
        implements IHrSchedulePlanService {

    private final IHrScheduleRuleService ruleService;
    private final IHrShiftService shiftService;

    @Override
    public Long createPlan(HrSchedulePlan plan) {
        log.info("创建排班计划: 员工={}, 日期={}", plan.getEmployeeId(), plan.getPlanDate());
        this.save(plan);
        return plan.getId();
    }

    @Override
    public Boolean batchCreatePlan(List<HrSchedulePlan> plans) {
        log.info("批量创建排班计划: {}条", plans.size());
        return this.saveBatch(plans);
    }

    @Override
    public Boolean updatePlan(HrSchedulePlan plan) {
        log.info("更新排班计划: {}", plan.getId());
        return this.updateById(plan);
    }

    @Override
    public Boolean deletePlan(Long planId) {
        log.info("删除排班计划: {}", planId);
        return this.removeById(planId);
    }

    @Override
    public List<HrSchedulePlan> getEmployeePlans(Long employeeId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HrSchedulePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSchedulePlan::getEmployeeId, employeeId);
        wrapper.ge(HrSchedulePlan::getPlanDate, startDate);
        wrapper.le(HrSchedulePlan::getPlanDate, endDate);
        wrapper.eq(HrSchedulePlan::getStatus, 1);
        wrapper.orderByAsc(HrSchedulePlan::getPlanDate);
        return this.list(wrapper);
    }

    @Override
    public HrSchedulePlan getPlanByDate(Long employeeId, LocalDate date) {
        LambdaQueryWrapper<HrSchedulePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSchedulePlan::getEmployeeId, employeeId);
        wrapper.eq(HrSchedulePlan::getPlanDate, date);
        wrapper.eq(HrSchedulePlan::getStatus, 1);
        wrapper.last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public Boolean generatePlanByRule(Long ruleId, LocalDate startDate, LocalDate endDate) {
        log.info("根据规则生成排班计划: 规则={}, {}~{}", ruleId, startDate, endDate);

        HrScheduleRule rule = ruleService.getById(ruleId);
        if (rule == null) {
            log.error("排班规则不存在: {}", ruleId);
            return false;
        }

        HrShift shift = shiftService.getById(rule.getShiftId());
        if (shift == null) {
            log.error("班次不存在: {}", rule.getShiftId());
            return false;
        }

        // 解析工作日
        List<String> workDayList = Arrays.asList(rule.getWorkDays().split(","));
        List<Integer> workDays = workDayList.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        // 生成排班计划
        List<HrSchedulePlan> plans = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            int dayValue = dayOfWeek.getValue();

            // 判断是否为工作日
            if (workDays.contains(dayValue)) {
                // TODO: 根据规则获取部门或岗位下的员工列表
                // 这里需要调用组织服务获取员工列表

                // 临时创建示例数据
                HrSchedulePlan plan = new HrSchedulePlan();
                plan.setPlanName(rule.getRuleName() + "-" + currentDate);
                plan.setShiftId(shift.getId());
                plan.setShiftName(shift.getShiftName());
                plan.setPlanDate(currentDate);
                plan.setDayOfWeek(dayValue);
                plan.setDeptId(rule.getDeptId());
                plan.setStatus(1);
                plan.setRemark("自动生成");

                plans.add(plan);
            }

            currentDate = currentDate.plusDays(1);
        }

        if (!plans.isEmpty()) {
            return this.saveBatch(plans);
        }

        return true;
    }
}
