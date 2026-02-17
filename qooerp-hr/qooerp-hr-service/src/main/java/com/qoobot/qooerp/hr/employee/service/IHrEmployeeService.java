package com.qoobot.qooerp.hr.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.employee.domain.HrEmployee;

import java.time.LocalDate;

/**
 * 员工服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IHrEmployeeService extends IService<HrEmployee> {

    /**
     * 查询员工详情
     *
     * @param id 员工ID
     * @return 员工详情
     */
    HrEmployee getEmployeeById(Long id);

    /**
     * 分页查询员工列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @return 员工分页列表
     */
    IPage<HrEmployee> getEmployeePage(long current, long size);

    /**
     * 创建员工
     *
     * @param employee 员工信息
     * @return 是否成功
     */
    boolean createEmployee(HrEmployee employee);

    /**
     * 更新员工
     *
     * @param employee 员工信息
     * @return 是否成功
     */
    boolean updateEmployee(HrEmployee employee);

    /**
     * 删除员工
     *
     * @param id 员工ID
     * @return 是否成功
     */
    boolean deleteEmployee(Long id);

    /**
     * 转正处理
     *
     * @param employeeId 员工ID
     * @param regularDate 转正日期
     * @return 是否成功
     */
    boolean becomeRegular(Long employeeId, LocalDate regularDate);

    /**
     * 离职处理
     *
     * @param employeeId 员工ID
     * @param resignationDate 离职日期
     * @return 是否成功
     */
    boolean resign(Long employeeId, LocalDate resignationDate);
}
