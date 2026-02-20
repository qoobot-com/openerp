package com.qoobot.qooerp.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.dto.PageQueryDTO;
import com.qoobot.qooerp.crm.dto.CrmServiceDTO;
import com.qoobot.qooerp.crm.entity.CrmCustomer;
import com.qoobot.qooerp.crm.entity.CrmService;
import com.qoobot.qooerp.crm.mapper.CrmCustomerMapper;
import com.qoobot.qooerp.crm.mapper.CrmServiceMapper;
import com.qoobot.qooerp.crm.service.CrmCustomerServiceService;
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
public class CrmCustomerServiceServiceImpl implements CrmCustomerServiceService {

    private final CrmServiceMapper serviceMapper;
    private final CrmCustomerMapper customerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmServiceDTO createService(CrmServiceDTO dto) {
        // 检查服务编号是否已存在
        LambdaQueryWrapper<CrmService> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmService::getServiceNo, dto.getServiceNo());
        wrapper.eq(CrmService::getDeleted, 0);
        if (serviceMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("服务编号已存在");
        }

        // 验证客户是否存在
        CrmCustomer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null || customer.getDeleted() == 1) {
            throw new RuntimeException("客户不存在");
        }
        dto.setCustomerName(customer.getCustomerName());

        CrmService entity = new CrmService();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        serviceMapper.insert(entity);

        CrmServiceDTO result = new CrmServiceDTO();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmServiceDTO updateService(Long id, CrmServiceDTO dto) {
        CrmService entity = serviceMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("服务不存在");
        }

        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        entity.setUpdateTime(LocalDateTime.now());
        serviceMapper.updateById(entity);

        CrmServiceDTO result = new CrmServiceDTO();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public CrmServiceDTO getService(Long id) {
        CrmService entity = serviceMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("服务不存在");
        }
        CrmServiceDTO dto = new CrmServiceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public IPage<CrmServiceDTO> listServices(PageQueryDTO pageParam, Long customerId, Integer status, Long assigneeId) {
        pageParam.checkAndSetDefault();
        Page<CrmService> page = pageParam.toPage();
        IPage<CrmService> entityPage = serviceMapper.selectPageWithCondition(page, customerId, status, assigneeId);

        return entityPage.convert(entity -> {
            CrmServiceDTO dto = new CrmServiceDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteService(Long id) {
        CrmService entity = serviceMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("服务不存在");
        }

        // 逻辑删除
        entity.setDeleted(1);
        entity.setUpdateTime(LocalDateTime.now());
        serviceMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignService(Long id, Long assigneeId, String assigneeName) {
        CrmService entity = serviceMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("服务不存在");
        }

        entity.setAssigneeId(assigneeId);
        entity.setAssigneeName(assigneeName);
        entity.setStatus(1);
        entity.setUpdateTime(LocalDateTime.now());
        serviceMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeService(Long id) {
        CrmService entity = serviceMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new RuntimeException("服务不存在");
        }

        entity.setStatus(2);
        entity.setUpdateTime(LocalDateTime.now());
        serviceMapper.updateById(entity);
    }
}
