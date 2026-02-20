package com.qoobot.qooerp.scm.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.scm.constant.ScmConstant;
import com.qoobot.qooerp.scm.customer.domain.ScmCustomer;
import com.qoobot.qooerp.scm.customer.domain.ScmCustomerGrade;
import com.qoobot.qooerp.scm.customer.dto.CustomerDTO;
import com.qoobot.qooerp.scm.customer.dto.CustomerQueryDTO;
import com.qoobot.qooerp.scm.customer.mapper.ScmCustomerGradeMapper;
import com.qoobot.qooerp.scm.customer.mapper.ScmCustomerMapper;
import com.qoobot.qooerp.scm.customer.service.IScmCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 客户Service实现
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScmCustomerServiceImpl extends ServiceImpl<ScmCustomerMapper, ScmCustomer> implements IScmCustomerService {

    private final ScmCustomerGradeMapper customerGradeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCustomer(CustomerDTO dto) {
        String customerCode = generateCustomerCode();

        ScmCustomer exist = getByCode(customerCode);
        if (exist != null) {
            throw new BusinessException("客户编码已存在");
        }

        ScmCustomer customer = BeanUtil.copyProperties(dto, ScmCustomer.class);
        customer.setCustomerCode(customerCode);
        customer.setCustomerLevel(ScmConstant.CUSTOMER_LEVEL_POTENTIAL);
        customer.setStatus(ScmConstant.STATUS_ACTIVE);
        customer.setTenantId(SecurityUtils.getTenantId());
        customer.setDeleted(0);

        save(customer);

        initCustomerGrade(customer.getId(), customerCode);

        log.info("创建客户成功，客户ID：{}，客户编码：{}", customer.getId(), customerCode);
        return customer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCustomer(Long id, CustomerDTO dto) {
        ScmCustomer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        ScmCustomer update = BeanUtil.copyProperties(dto, ScmCustomer.class);
        update.setId(id);
        update.setCustomerCode(customer.getCustomerCode());

        boolean result = updateById(update);
        log.info("更新客户成功，客户ID：{}", id);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCustomer(Long id) {
        ScmCustomer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        boolean result = removeById(id);
        log.info("删除客户成功，客户ID：{}", id);
        return result;
    }

    @Override
    public CustomerDTO getCustomer(Long id) {
        ScmCustomer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return BeanUtil.copyProperties(customer, CustomerDTO.class);
    }

    @Override
    public PageResult<ScmCustomer> queryCustomers(CustomerQueryDTO queryDTO) {
        Page<ScmCustomer> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<ScmCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getStatus() != null, ScmCustomer::getStatus, queryDTO.getStatus())
                .like(queryDTO.getCustomerCode() != null, ScmCustomer::getCustomerCode, queryDTO.getCustomerCode())
                .like(queryDTO.getCustomerName() != null, ScmCustomer::getCustomerName, queryDTO.getCustomerName())
                .eq(queryDTO.getCustomerType() != null, ScmCustomer::getCustomerType, queryDTO.getCustomerType())
                .eq(queryDTO.getCustomerLevel() != null, ScmCustomer::getCustomerLevel, queryDTO.getCustomerLevel())
                .orderByDesc(ScmCustomer::getCreateTime);

        IPage<ScmCustomer> result = page(page, wrapper);

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        ScmCustomer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        customer.setStatus(status);
        boolean result = updateById(customer);
        log.info("更新客户状态成功，客户ID：{}，状态：{}", id, status);
        return result;
    }

    @Override
    public String generateCustomerCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomNum = String.format("%03d", IdUtil.getSnowflakeNextId() % 1000);
        return "CU" + date + randomNum;
    }

    private void initCustomerGrade(Long customerId, String customerCode) {
        ScmCustomerGrade grade = new ScmCustomerGrade();
        grade.setCustomerId(customerId);
        grade.setCustomerCode(customerCode);
        grade.setGradeDate(LocalDate.now());
        grade.setCustomerLevel(ScmConstant.CUSTOMER_LEVEL_POTENTIAL);
        grade.setAnnualPurchaseAmount(java.math.BigDecimal.ZERO);
        grade.setPurchaseCount(0);
        grade.setTenantId(SecurityUtils.getTenantId());
        grade.setDeleted(0);

        customerGradeMapper.insert(grade);
    }

    private ScmCustomer getByCode(String customerCode) {
        LambdaQueryWrapper<ScmCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScmCustomer::getCustomerCode, customerCode);
        return getOne(wrapper);
    }
}
