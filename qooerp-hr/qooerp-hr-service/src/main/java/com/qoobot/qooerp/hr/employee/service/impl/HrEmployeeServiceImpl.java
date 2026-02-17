package com.qoobot.qooerp.hr.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.enums.CommonStatus;
import com.qoobot.qooerp.common.util.RedisCacheUtil;
import com.qoobot.qooerp.hr.employee.domain.HrEmployee;
import com.qoobot.qooerp.hr.employee.mapper.HrEmployeeMapper;
import com.qoobot.qooerp.hr.employee.service.IHrEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工服务实现
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrEmployeeServiceImpl extends ServiceImpl<HrEmployeeMapper, HrEmployee>
        implements IHrEmployeeService {

    private static final String EMPLOYEE_CACHE_PREFIX = "hr:employee:";

    private final HrEmployeeMapper employeeMapper;
    private final RedisCacheUtil redisCacheUtil;

    @Override
    public HrEmployee getEmployeeById(Long id) {
        String cacheKey = EMPLOYEE_CACHE_PREFIX + id;
        HrEmployee employee = redisCacheUtil.get(cacheKey, HrEmployee.class);
        if (employee == null) {
            employee = employeeMapper.selectById(id);
            if (employee != null) {
                redisCacheUtil.set(cacheKey, employee, 30);
            }
        }
        return employee;
    }

    @Override
    public IPage<HrEmployee> getEmployeePage(long current, long size) {
        Page<HrEmployee> page = new Page<>(current, size);
        LambdaQueryWrapper<HrEmployee> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(HrEmployee::getCreateTime);
        return employeeMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean createEmployee(HrEmployee employee) {
        employee.setStatus(CommonStatus.ENABLED.getCode());
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        boolean result = employeeMapper.insert(employee) > 0;
        if (result) {
            // 清除缓存
            redisCacheUtil.delete(EMPLOYEE_CACHE_PREFIX + employee.getId());
        }
        return result;
    }

    @Override
    public boolean updateEmployee(HrEmployee employee) {
        employee.setUpdateTime(LocalDateTime.now());
        boolean result = employeeMapper.updateById(employee) > 0;
        if (result) {
            // 清除缓存
            redisCacheUtil.delete(EMPLOYEE_CACHE_PREFIX + employee.getId());
        }
        return result;
    }

    @Override
    public boolean deleteEmployee(Long id) {
        boolean result = employeeMapper.deleteById(id) > 0;
        if (result) {
            // 清除缓存
            redisCacheUtil.delete(EMPLOYEE_CACHE_PREFIX + id);
        }
        return result;
    }

    @Override
    public boolean becomeRegular(Long employeeId, LocalDate regularDate) {
        HrEmployee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            log.warn("员工不存在: {}", employeeId);
            return false;
        }
        employee.setRegularDate(regularDate);
        employee.setStatus(CommonStatus.REGULAR.getCode());
        employee.setUpdateTime(LocalDateTime.now());
        boolean result = employeeMapper.updateById(employee) > 0;
        if (result) {
            redisCacheUtil.delete(EMPLOYEE_CACHE_PREFIX + employeeId);
        }
        return result;
    }

    @Override
    public boolean resign(Long employeeId, LocalDate resignationDate) {
        HrEmployee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            log.warn("员工不存在: {}", employeeId);
            return false;
        }
        employee.setResignationDate(resignationDate);
        employee.setStatus(CommonStatus.RESIGNED.getCode());
        employee.setUpdateTime(LocalDateTime.now());
        boolean result = employeeMapper.updateById(employee) > 0;
        if (result) {
            redisCacheUtil.delete(EMPLOYEE_CACHE_PREFIX + employeeId);
        }
        return result;
    }
}
