package com.qoobot.qooerp.hr.salary.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.salary.domain.HrSalaryAdjust;
import com.qoobot.qooerp.common.result.Result;

/**
 * 薪酬调整服务接口
 */
public interface IHrSalaryAdjustService extends IService<HrSalaryAdjust> {

    /**
     * 提交薪资调整申请
     */
    Result<HrSalaryAdjust> submitAdjust(HrSalaryAdjust adjust);

    /**
     * 审批薪资调整
     */
    Result<Boolean> approveAdjust(Long adjustId, Boolean approved, String remark);

    /**
     * 撤销待审批的调整申请
     */
    Result<Boolean> cancelAdjust(Long adjustId);

    /**
     * 获取调整详情
     */
    Result<HrSalaryAdjust> getAdjust(Long id);

    /**
     * 分页查询员工调整记录
     */
    Result<IPage<HrSalaryAdjust>> listEmployeeAdjusts(IPage<?> page, Long employeeId);

    /**
     * 分页查询待审批调整列表
     */
    Result<IPage<HrSalaryAdjust>> listPendingApprovals(IPage<?> page);
}
