package com.qoobot.qooerp.hr.salary.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.salary.domain.HrSalary;
import com.qoobot.qooerp.common.result.Result;

import java.util.List;

/**
 * 薪资发放服务接口
 */
public interface IHrSalaryService extends IService<HrSalary> {

    /**
     * 计算员工薪资
     */
    Result<HrSalary> calculateSalary(Long employeeId, String salaryMonth);

    /**
     * 批量计算部门薪资
     */
    Result<List<HrSalary>> batchCalculateSalary(Long departmentId, String salaryMonth);

    /**
     * 发放薪资
     */
    Result<Boolean> paySalary(Long salaryId);

    /**
     * 批量发放薪资
     */
    Result<Boolean> batchPaySalary(List<Long> salaryIds);

    /**
     * 作废薪资单
     */
    Result<Boolean> cancelSalary(Long salaryId);

    /**
     * 获取薪资详情
     */
    Result<HrSalary> getSalary(Long id);

    /**
     * 根据员工和月份获取薪资
     */
    Result<HrSalary> getSalaryByEmployeeAndMonth(Long employeeId, String salaryMonth);

    /**
     * 分页查询员工薪资历史
     */
    Result<IPage<HrSalary>> listEmployeeSalaries(IPage<?> page, Long employeeId);

    /**
     * 分页查询月度薪资列表
     */
    Result<IPage<HrSalary>> listMonthlySalaries(IPage<?> page, String salaryMonth);

    /**
     * 计算个人所得税
     */
    java.math.BigDecimal calculatePersonalTax(java.math.BigDecimal taxableIncome);
}
