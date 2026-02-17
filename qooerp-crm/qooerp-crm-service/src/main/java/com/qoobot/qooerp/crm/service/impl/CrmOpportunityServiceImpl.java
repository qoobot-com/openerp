package com.qoobot.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.core.page.PageParam;
import com.qoobot.qooerp.crm.dto.CrmContractDTO;
import com.qoobot.qooerp.crm.dto.CrmOpportunityDTO;
import com.qoobot.qooerp.crm.entity.CrmContract;
import com.qoobot.qooerp.crm.entity.CrmCustomer;
import com.qoobot.qooerp.crm.entity.CrmOpportunity;
import com.qoobot.qooerp.crm.mapper.CrmContractMapper;
import com.qoobot.qooerp.crm.mapper.CrmCustomerMapper;
import com.qoobot.qooerp.crm.mapper.CrmOpportunityMapper;
import com.qoobot.qooerp.crm.service.CrmContractService;
import com.qoobot.qooerp.crm.service.CrmOpportunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商机服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrmOpportunityServiceImpl implements CrmOpportunityService {

    private final CrmOpportunityMapper opportunityMapper;
    private final CrmCustomerMapper customerMapper;
    private final CrmContractMapper contractMapper;
    private final CrmContractService contractService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmOpportunityDTO createOpportunity(CrmOpportunityDTO dto) {
        // 检查商机编号是否已存在
        LambdaQueryWrapper<CrmOpportunity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmOpportunity::getOpportunityNo, dto.getOpportunityNo());
        wrapper.eq(CrmOpportunity::getDeleted, 0);
        if (opportunityMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("商机编号已存在");
        }

        // 验证客户是否存在
        CrmCustomer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null || customer.getDeleted() == 1) {
            throw new RuntimeException("客户不存在");
        }
        dto.setCustomerName(customer.getCustomerName());

        CrmOpportunity entity = new CrmOpportunity();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getStage() == null) {
            entity.setStage(1);
        }
        opportunityMapper.insert(entity);

        CrmOpportunityDTO result = new CrmOpportunityDTO();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmOpportunityDTO updateOpportunity(Long id, CrmOpportunityDTO dto) {
        CrmOpportunity entity = opportunityMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("商机不存在");
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
        opportunityMapper.updateById(entity);

        CrmOpportunityDTO result = new CrmOpportunityDTO();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public CrmOpportunityDTO getOpportunity(Long id) {
        CrmOpportunity entity = opportunityMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("商机不存在");
        }
        CrmOpportunityDTO dto = new CrmOpportunityDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public IPage<CrmOpportunityDTO> listOpportunities(PageParam pageParam, Long customerId, Integer stage, Integer status, Long ownerId) {
        Page<CrmOpportunity> page = new Page<>(pageParam.getPage(), pageParam.getSize());
        IPage<CrmOpportunity> entityPage = opportunityMapper.selectPageWithCondition(page, customerId, stage, status, ownerId);

        return entityPage.convert(entity -> {
            CrmOpportunityDTO dto = new CrmOpportunityDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOpportunity(Long id) {
        CrmOpportunity entity = opportunityMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("商机不存在");
        }

        // 逻辑删除
        entity.setDeleted(1);
        entity.setUpdateTime(LocalDateTime.now());
        opportunityMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStage(Long id, Integer stage) {
        CrmOpportunity entity = opportunityMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("商机不存在");
        }

        entity.setStage(stage);
        entity.setUpdateTime(LocalDateTime.now());
        opportunityMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long convertToContract(Long opportunityId) {
        CrmOpportunity opportunity = opportunityMapper.selectById(opportunityId);
        if (opportunity == null || opportunity.getDeleted() == 1) {
            throw new RuntimeException("商机不存在");
        }

        if (opportunity.getStage() < 5) {
            throw new RuntimeException("商机未到合同签订阶段");
        }

        if (opportunity.getStatus() == 1) {
            throw new RuntimeException("商机已转化");
        }

        // 创建合同
        CrmContractDTO contractDTO = new CrmContractDTO();
        contractDTO.setContractNo("CON" + System.currentTimeMillis());
        contractDTO.setOpportunityId(opportunityId);
        contractDTO.setCustomerId(opportunity.getCustomerId());
        contractDTO.setCustomerName(opportunity.getCustomerName());
        contractDTO.setContractName(opportunity.getOpportunityName() + "合同");
        contractDTO.setContractAmount(opportunity.getAmount());
        contractDTO.setStartDate(LocalDate.now());
        contractDTO.setEndDate(LocalDate.now().plusYears(1));
        contractDTO.setStatus(0);

        CrmContractDTO contract = contractService.createContract(contractDTO);

        // 更新商机状态
        opportunity.setStatus(1);
        opportunity.setUpdateTime(LocalDateTime.now());
        opportunityMapper.updateById(opportunity);

        return contract.getId();
    }

    @Override
    public List<CrmOpportunityDTO> getCustomerOpportunities(Long customerId) {
        LambdaQueryWrapper<CrmOpportunity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmOpportunity::getCustomerId, customerId);
        wrapper.eq(CrmOpportunity::getDeleted, 0);
        wrapper.orderByDesc(CrmOpportunity::getCreateTime);

        return opportunityMapper.selectList(wrapper).stream().map(entity -> {
            CrmOpportunityDTO dto = new CrmOpportunityDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}
