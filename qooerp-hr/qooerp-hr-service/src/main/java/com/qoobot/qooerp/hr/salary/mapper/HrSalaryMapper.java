package com.qoobot.qooerp.hr.salary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.hr.salary.domain.HrSalary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 薪资发放Mapper
 */
@Mapper
public interface HrSalaryMapper extends BaseMapper<HrSalary> {

    /**
     * 根据员工ID和薪资年月查询薪资
     */
    HrSalary selectByEmployeeAndMonth(@Param("employeeId") Long employeeId, @Param("salaryMonth") String salaryMonth);

    /**
     * 分页查询员工薪资列表
     */
    IPage<HrSalary> selectEmployeeSalaryPage(Page<?> page, @Param("employeeId") Long employeeId);

    /**
     * 分页查询薪资发放列表（按月份）
     */
    IPage<HrSalary> selectSalaryPage(Page<?> page, @Param("salaryMonth") String salaryMonth);
}
