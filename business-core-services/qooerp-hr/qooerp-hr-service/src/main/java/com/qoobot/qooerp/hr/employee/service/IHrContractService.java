package com.qoobot.qooerp.hr.employee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.employee.domain.HrContract;

import java.time.LocalDate;

/**
 * 员工合同服务接口
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
public interface IHrContractService extends IService<HrContract> {

    /**
     * 创建合同
     */
    Long createContract(HrContract contract);

    /**
     * 更新合同
     */
    void updateContract(HrContract contract);

    /**
     * 删除合同
     */
    void deleteContract(Long contractId);

    /**
     * 查询合同详情
     */
    HrContract getContractById(Long contractId);

    /**
     * 查询员工合同列表
     */
    java.util.List<HrContract> getContractsByEmployeeId(Long employeeId);

    /**
     * 检查合同是否到期
     */
    boolean checkContractExpiry(Long contractId);
}
