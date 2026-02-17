package com.qoobot.qooerp.hr.employee.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.employee.domain.HrContract;
import com.qoobot.qooerp.hr.employee.mapper.HrContractMapper;
import com.qoobot.qooerp.hr.employee.service.IHrContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 员工合同服务实现
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrContractServiceImpl extends ServiceImpl<HrContractMapper, HrContract>
        implements IHrContractService {

    private final HrContractMapper contractMapper;

    @Override
    public Long createContract(HrContract contract) {
        contract.setStatus(1); // 默认生效中
        contractMapper.insert(contract);
        return contract.getId();
    }

    @Override
    public void updateContract(HrContract contract) {
        contractMapper.updateById(contract);
    }

    @Override
    public void deleteContract(Long contractId) {
        contractMapper.deleteById(contractId);
    }

    @Override
    public HrContract getContractById(Long contractId) {
        return contractMapper.selectById(contractId);
    }

    @Override
    public List<HrContract> getContractsByEmployeeId(Long employeeId) {
        return contractMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HrContract>()
                .eq(HrContract::getEmployeeId, employeeId)
                .orderByDesc(HrContract::getCreateTime)
        );
    }

    @Override
    public boolean checkContractExpiry(Long contractId) {
        HrContract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        // 检查合同是否到期
        if (contract.getEndDate() != null && contract.getEndDate().isBefore(today)) {
            // 更新合同状态为已到期
            contract.setStatus(2);
            contractMapper.updateById(contract);
            return true;
        }
        return false;
    }
}
