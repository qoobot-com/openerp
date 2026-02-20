package com.qoobot.qooerp.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.dto.PageQueryDTO;
import com.qoobot.qooerp.crm.dto.CrmContractDTO;
import com.qoobot.qooerp.crm.entity.CrmContract;
import com.qoobot.qooerp.crm.entity.CrmCustomer;
import com.qoobot.qooerp.crm.mapper.CrmContractMapper;
import com.qoobot.qooerp.crm.mapper.CrmCustomerMapper;
import com.qoobot.qooerp.crm.service.CrmContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrmContractServiceImpl implements CrmContractService {

    private final CrmContractMapper contractMapper;
    private final CrmCustomerMapper customerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmContractDTO createContract(CrmContractDTO dto) {
        // 检查合同编号是否已存在
        LambdaQueryWrapper<CrmContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmContract::getContractNo, dto.getContractNo());
        wrapper.eq(CrmContract::getDeleted, 0);
        if (contractMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("合同编号已存在");
        }

        // 验证客户是否存在
        CrmCustomer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null || customer.getDeleted() == 1) {
            throw new RuntimeException("客户不存在");
        }
        dto.setCustomerName(customer.getCustomerName());

        CrmContract entity = new CrmContract();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        contractMapper.insert(entity);

        CrmContractDTO result = new CrmContractDTO();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmContractDTO updateContract(Long id, CrmContractDTO dto) {
        CrmContract entity = contractMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("合同不存在");
        }

        // 如果客户ID变更，验证新客户是否存在
        if (!entity.getCustomerId().equals(dto.getCustomerId())) {
            CrmCustomer customer = customerMapper.selectById(dto.getCustomerId());
            if (customer == null || customer.getDeleted() == 1) {
                throw new RuntimeException("客户不存在");
            }
            dto.setCustomerName(customer.getCustomerName());
        }

        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        entity.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(entity);

        CrmContractDTO result = new CrmContractDTO();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public CrmContractDTO getContract(Long id) {
        CrmContract entity = contractMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("合同不存在");
        }
        CrmContractDTO dto = new CrmContractDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public IPage<CrmContractDTO> listContracts(PageQueryDTO pageParam, Long customerId, Integer status) {
        pageParam.checkAndSetDefault();
        Page<CrmContract> page = pageParam.toPage();
        IPage<CrmContract> entityPage = contractMapper.selectPageWithCondition(page, customerId, status);

        return entityPage.convert(entity -> {
            CrmContractDTO dto = new CrmContractDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContract(Long id) {
        CrmContract entity = contractMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("合同不存在");
        }

        // 逻辑删除
        entity.setDeleted(1);
        entity.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveContract(Long id) {
        CrmContract entity = contractMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("合同不存在");
        }

        if (entity.getStatus() != 0) {
            throw new RuntimeException("只有草稿状态的合同才能审核");
        }

        entity.setStatus(1);
        entity.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectContract(Long id) {
        CrmContract entity = contractMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("合同不存在");
        }

        if (entity.getStatus() != 0) {
            throw new RuntimeException("只有草稿状态的合同才能拒绝");
        }

        // 拒绝后删除合同
        entity.setDeleted(1);
        entity.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(entity);
    }
}
