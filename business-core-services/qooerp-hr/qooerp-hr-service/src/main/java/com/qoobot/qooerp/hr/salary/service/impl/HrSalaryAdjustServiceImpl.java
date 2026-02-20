package com.qoobot.qooerp.hr.salary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.salary.domain.HrSalaryAdjust;
import com.qoobot.qooerp.hr.salary.mapper.HrSalaryAdjustMapper;
import com.qoobot.qooerp.hr.salary.service.IHrSalaryAdjustService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 薪酬调整服务实现
 */
@Service
public class HrSalaryAdjustServiceImpl extends ServiceImpl<HrSalaryAdjustMapper, HrSalaryAdjust> 
        implements IHrSalaryAdjustService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<HrSalaryAdjust> submitAdjust(HrSalaryAdjust adjust) {
        adjust.setAdjustNo("SALAJ" + System.currentTimeMillis() + (int)(Math.random() * 1000));
        adjust.setStatus(0); // 待审批
        adjust.setCreateTime(LocalDateTime.now());
        adjust.setUpdateTime(LocalDateTime.now());

        // 计算工资变动额
        BigDecimal beforeTotal = (adjust.getBeforeBaseSalary() != null ? adjust.getBeforeBaseSalary() : BigDecimal.ZERO)
                .add(adjust.getBeforePostSalary() != null ? adjust.getBeforePostSalary() : BigDecimal.ZERO)
                .add(adjust.getBeforePerformanceSalary() != null ? adjust.getBeforePerformanceSalary() : BigDecimal.ZERO);
        BigDecimal afterTotal = (adjust.getAfterBaseSalary() != null ? adjust.getAfterBaseSalary() : BigDecimal.ZERO)
                .add(adjust.getAfterPostSalary() != null ? adjust.getAfterPostSalary() : BigDecimal.ZERO)
                .add(adjust.getAfterPerformanceSalary() != null ? adjust.getAfterPerformanceSalary() : BigDecimal.ZERO);
        adjust.setSalaryChange(afterTotal.subtract(beforeTotal));

        boolean success = this.save(adjust);
        return success ? Result.success(adjust) : Result.error("提交失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> approveAdjust(Long adjustId, Boolean approved, String remark) {
        HrSalaryAdjust adjust = this.getById(adjustId);
        if (adjust == null) {
            return Result.error("调整记录不存在");
        }
        if (adjust.getStatus() != 0) {
            return Result.error("该调整已处理");
        }

        adjust.setStatus(approved ? 1 : 2); // 已通过/已拒绝
        adjust.setApproveDate(LocalDate.now());
        adjust.setRemark(remark);
        adjust.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(adjust);
        return success ? Result.success(true) : Result.error("审批失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> cancelAdjust(Long adjustId) {
        HrSalaryAdjust adjust = this.getById(adjustId);
        if (adjust == null) {
            return Result.error("调整记录不存在");
        }
        if (adjust.getStatus() != 0) {
            return Result.error("该调整已处理，无法撤销");
        }

        adjust.setStatus(3); // 已取消
        adjust.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(adjust);
        return success ? Result.success(true) : Result.error("撤销失败");
    }

    @Override
    public Result<HrSalaryAdjust> getAdjust(Long id) {
        HrSalaryAdjust adjust = this.getById(id);
        return adjust != null ? Result.success(adjust) : Result.error("调整记录不存在");
    }

    @Override
    public Result<IPage<HrSalaryAdjust>> listEmployeeAdjusts(IPage<?> page, Long employeeId) {
        LambdaQueryWrapper<HrSalaryAdjust> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSalaryAdjust::getEmployeeId, employeeId);
        wrapper.orderByDesc(HrSalaryAdjust::getCreateTime);

        IPage<HrSalaryAdjust> result = this.page((Page<HrSalaryAdjust>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<IPage<HrSalaryAdjust>> listPendingApprovals(IPage<?> page) {
        LambdaQueryWrapper<HrSalaryAdjust> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSalaryAdjust::getStatus, 0); // 待审批
        wrapper.orderByAsc(HrSalaryAdjust::getCreateTime);

        IPage<HrSalaryAdjust> result = this.page((Page<HrSalaryAdjust>) page, wrapper);
        return Result.success(result);
    }
}
