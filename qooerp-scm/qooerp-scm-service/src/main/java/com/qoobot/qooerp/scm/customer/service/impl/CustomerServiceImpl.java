package com.qoobot.qooerp.scm.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.customer.domain.Customer;
import com.qoobot.qooerp.scm.customer.dto.CustomerDTO;
import com.qoobot.qooerp.scm.customer.dto.CustomerQueryDTO;
import com.qoobot.qooerp.scm.customer.mapper.CustomerMapper;
import com.qoobot.qooerp.scm.customer.service.ICustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 客户Service实现
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

    private static final String CUSTOMER_CODE_PREFIX = "CST";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CustomerDTO dto) {
        // 生成客户编码
        if (!StringUtils.hasText(dto.getCustomerCode())) {
            dto.setCustomerCode(generateCustomerCode());
        }

        // 校验编码唯一性
        Customer existCustomer = getByCode(dto.getCustomerCode());
        if (existCustomer != null) {
            throw new BusinessException("客户编码已存在：" + dto.getCustomerCode());
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);

        if (customer.getStatus() == null) {
            customer.setStatus("ENABLED");
        }

        save(customer);
        return customer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(CustomerDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("客户ID不能为空");
        }

        Customer customer = getById(dto.getId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        // 如果修改了编码，需要校验唯一性
        if (StringUtils.hasText(dto.getCustomerCode()) &&
            !dto.getCustomerCode().equals(customer.getCustomerCode())) {
            Customer existCustomer = getByCode(dto.getCustomerCode());
            if (existCustomer != null && !existCustomer.getId().equals(dto.getId())) {
                throw new BusinessException("客户编码已存在：" + dto.getCustomerCode());
            }
        }

        BeanUtils.copyProperties(dto, customer);
        return updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return removeById(id);
    }

    @Override
    public CustomerDTO getDetail(Long id) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }

    @Override
    public PageResult<Customer> queryPage(CustomerQueryDTO queryDTO) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getCustomerCode())) {
            wrapper.like(Customer::getCustomerCode, queryDTO.getCustomerCode());
        }
        if (StringUtils.hasText(queryDTO.getCustomerName())) {
            wrapper.like(Customer::getCustomerName, queryDTO.getCustomerName());
        }
        if (StringUtils.hasText(queryDTO.getCustomerType())) {
            wrapper.eq(Customer::getCustomerType, queryDTO.getCustomerType());
        }
        if (StringUtils.hasText(queryDTO.getCreditRating())) {
            wrapper.eq(Customer::getCreditRating, queryDTO.getCreditRating());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(Customer::getStatus, queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getContactPerson())) {
            wrapper.like(Customer::getContactPerson, queryDTO.getContactPerson());
        }
        if (StringUtils.hasText(queryDTO.getContactPhone())) {
            wrapper.like(Customer::getContactPhone, queryDTO.getContactPhone());
        }
        if (StringUtils.hasText(queryDTO.getProvince())) {
            wrapper.eq(Customer::getProvince, queryDTO.getProvince());
        }
        if (StringUtils.hasText(queryDTO.getCity())) {
            wrapper.eq(Customer::getCity, queryDTO.getCity());
        }

        wrapper.orderByDesc(Customer::getCreateTime);

        Page<Customer> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        Page<Customer> result = page(page, wrapper);

        return new PageResult<>(page.getCurrent(), page.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public Customer getByCode(String customerCode) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getCustomerCode, customerCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        customer.setStatus(status);
        return updateById(customer);
    }

    @Override
    public String generateCustomerCode() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String prefix = CUSTOMER_CODE_PREFIX + dateStr;

        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Customer::getCustomerCode, prefix);
        wrapper.orderByDesc(Customer::getCustomerCode);
        wrapper.last("LIMIT 1");

        Customer lastCustomer = getOne(wrapper);

        int seq = 1;
        if (lastCustomer != null && StringUtils.hasText(lastCustomer.getCustomerCode())) {
            String lastCode = lastCustomer.getCustomerCode();
            try {
                String seqStr = lastCode.substring(prefix.length());
                seq = Integer.parseInt(seqStr) + 1;
            } catch (Exception e) {
                // 解析失败，从1开始
            }
        }

        return String.format("%s%04d", prefix, seq);
    }
}
