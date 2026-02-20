package com.qoobot.qooerp.hr.salary.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.salary.domain.HrSalaryStructure;
import com.qoobot.qooerp.common.result.Result;

/**
 * 薪酬结构服务接口
 */
public interface IHrSalaryStructureService extends IService<HrSalaryStructure> {

    /**
     * 创建薪酬方案
     */
    Result<HrSalaryStructure> createStructure(HrSalaryStructure structure);

    /**
     * 更新薪酬方案
     */
    Result<Boolean> updateStructure(HrSalaryStructure structure);

    /**
     * 删除薪酬方案
     */
    Result<Boolean> deleteStructure(Long id);

    /**
     * 获取薪酬方案详情
     */
    Result<HrSalaryStructure> getStructure(Long id);

    /**
     * 分页查询薪酬方案列表
     */
    Result<IPage<HrSalaryStructure>> listStructures(IPage<?> page, String structureName, Boolean isEnabled);

    /**
     * 获取启用的薪酬方案列表
     */
    Result<java.util.List<HrSalaryStructure>> listEnabledStructures();

    /**
     * 根据部门获取薪酬方案
     */
    Result<HrSalaryStructure> getStructureByDepartment(Long departmentId);

    /**
     * 根据岗位获取薪酬方案
     */
    Result<HrSalaryStructure> getStructureByPosition(Long positionId);
}
