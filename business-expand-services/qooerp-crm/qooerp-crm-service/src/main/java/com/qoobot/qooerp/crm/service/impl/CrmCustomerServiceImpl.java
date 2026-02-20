package com.qoobot.qooerp.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.dto.PageQueryDTO;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.common.enums.ResponseCode;
import com.qoobot.qooerp.crm.dto.CrmCustomerDTO;
import com.qoobot.qooerp.crm.dto.CrmFollowupDTO;
import com.qoobot.qooerp.crm.entity.CrmCustomer;
import com.qoobot.qooerp.crm.entity.CrmFollowup;
import com.qoobot.qooerp.crm.mapper.CrmCustomerMapper;
import com.qoobot.qooerp.crm.mapper.CrmFollowupMapper;
import com.qoobot.qooerp.crm.service.CrmCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 客户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrmCustomerServiceImpl implements CrmCustomerService {

    private final CrmCustomerMapper customerMapper;
    private final CrmFollowupMapper followupMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmCustomerDTO createCustomer(CrmCustomerDTO dto) {
        // 检查客户编号是否已存在
        LambdaQueryWrapper<CrmCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmCustomer::getCustomerNo, dto.getCustomerNo());
        wrapper.eq(CrmCustomer::getDeleted, 0);
        if (customerMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("客户编号已存在");
        }

        CrmCustomer entity = new CrmCustomer();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        customerMapper.insert(entity);

        CrmCustomerDTO result = new CrmCustomerDTO();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmCustomerDTO updateCustomer(Long id, CrmCustomerDTO dto) {
        CrmCustomer entity = customerMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("客户不存在");
        }

        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        entity.setUpdateTime(LocalDateTime.now());
        customerMapper.updateById(entity);

        CrmCustomerDTO result = new CrmCustomerDTO();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public CrmCustomerDTO getCustomer(Long id) {
        CrmCustomer entity = customerMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("客户不存在");
        }
        CrmCustomerDTO dto = new CrmCustomerDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public IPage<CrmCustomerDTO> listCustomers(PageQueryDTO pageParam, String customerName, Integer customerType, Integer level, Integer status) {
        pageParam.checkAndSetDefault();
        Page<CrmCustomer> page = pageParam.toPage();
        IPage<CrmCustomer> entityPage = customerMapper.selectPageWithCondition(page, customerName, customerType, level, status);

        return entityPage.convert(entity -> {
            CrmCustomerDTO dto = new CrmCustomerDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(Long id) {
        CrmCustomer entity = customerMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("客户不存在");
        }

        // 逻辑删除
        entity.setDeleted(1);
        entity.setUpdateTime(LocalDateTime.now());
        customerMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFollowup(Long customerId, CrmFollowupDTO dto) {
        // 验证客户是否存在
        CrmCustomer customer = customerMapper.selectById(customerId);
        if (customer == null || customer.getDeleted() == 1) {
            throw new RuntimeException("客户不存在");
        }

        CrmFollowup entity = new CrmFollowup();
        BeanUtils.copyProperties(dto, entity);
        entity.setCustomerId(customerId);
        entity.setCreateTime(LocalDateTime.now());
        entity.setDeleted(0);
        followupMapper.insert(entity);
    }
}
