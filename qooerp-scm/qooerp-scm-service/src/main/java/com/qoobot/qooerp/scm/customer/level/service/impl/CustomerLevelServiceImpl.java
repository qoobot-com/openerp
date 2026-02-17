package com.qoobot.qooerp.scm.customer.level.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.customer.level.domain.CustomerLevel;
import com.qoobot.qooerp.scm.customer.level.dto.CustomerLevelDTO;
import com.qoobot.qooerp.scm.customer.level.mapper.CustomerLevelMapper;
import com.qoobot.qooerp.scm.customer.level.service.ICustomerLevelService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * 客户等级Service实现
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Service
public class CustomerLevelServiceImpl extends ServiceImpl<CustomerLevelMapper, CustomerLevel>
        implements ICustomerLevelService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CustomerLevelDTO dto) {
        // 校验编码唯一性
        if (checkCodeExists(dto.getLevelCode(), null)) {
            throw new BusinessException("客户等级编码已存在：" + dto.getLevelCode());
        }

        // 设置默认优先级
        if (dto.getPriority() == null) {
            dto.setPriority(0);
        }

        // 设置默认折扣率
        if (dto.getDiscountRate() == null) {
            dto.setDiscountRate(BigDecimal.ZERO);
        }

        // 设置默认状态
        if (!StringUtils.hasText(dto.getStatus())) {
            dto.setStatus("ENABLED");
        }

        CustomerLevel level = new CustomerLevel();
        BeanUtils.copyProperties(dto, level);

        save(level);
        return level.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(CustomerLevelDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("等级ID不能为空");
        }

        CustomerLevel level = getById(dto.getId());
        if (level == null) {
            throw new BusinessException("客户等级不存在");
        }

        // 如果修改了编码，需要校验唯一性
        if (StringUtils.hasText(dto.getLevelCode()) &&
            !dto.getLevelCode().equals(level.getLevelCode())) {
            if (checkCodeExists(dto.getLevelCode(), dto.getId())) {
                throw new BusinessException("客户等级编码已存在：" + dto.getLevelCode());
            }
        }

        BeanUtils.copyProperties(dto, level);
        return updateById(level);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        CustomerLevel level = getById(id);
        if (level == null) {
            throw new BusinessException("客户等级不存在");
        }

        // TODO: 检查是否有关联的客户
        // if (hasRelatedCustomers(id)) {
        //     throw new BusinessException("该等级下存在关联的客户，不能删除");
        // }

        return removeById(id);
    }

    @Override
    public CustomerLevelDTO getDetail(Long id) {
        CustomerLevel level = getById(id);
        if (level == null) {
            throw new BusinessException("客户等级不存在");
        }

        CustomerLevelDTO dto = new CustomerLevelDTO();
        BeanUtils.copyProperties(level, dto);
        return dto;
    }

    @Override
    public PageResult<CustomerLevel> queryPage(Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<CustomerLevel> wrapper = new LambdaQueryWrapper<>();

        wrapper.orderByAsc(CustomerLevel::getPriority)
               .orderByAsc(CustomerLevel::getLevelCode);

        Page<CustomerLevel> page = new Page<>(pageNo, pageSize);
        Page<CustomerLevel> result = page(page, wrapper);

        return new PageResult<>(page.getCurrent(), page.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public List<CustomerLevel> getAllEnabledLevels() {
        LambdaQueryWrapper<CustomerLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerLevel::getStatus, "ENABLED");
        wrapper.orderByDesc(CustomerLevel::getPriority)
               .orderByAsc(CustomerLevel::getLevelCode);
        return list(wrapper);
    }

    @Override
    public CustomerLevel determineCustomerLevel(BigDecimal totalAmount, Integer orderCount) {
        List<CustomerLevel> allLevels = getAllEnabledLevels();

        if (allLevels.isEmpty()) {
            throw new BusinessException("暂无可用的客户等级");
        }

        // 按优先级从高到低匹配
        return allLevels.stream()
                .filter(level -> matchLevel(level, totalAmount, orderCount))
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断是否匹配等级条件
     */
    private boolean matchLevel(CustomerLevel level, BigDecimal totalAmount, Integer orderCount) {
        // 检查交易金额范围
        if (level.getMinAmount() != null && totalAmount.compareTo(level.getMinAmount()) < 0) {
            return false;
        }
        if (level.getMaxAmount() != null && totalAmount.compareTo(level.getMaxAmount()) > 0) {
            return false;
        }

        // 检查订单数
        if (level.getMinOrders() != null && orderCount < level.getMinOrders()) {
            return false;
        }

        return true;
    }

    @Override
    public CustomerLevel getByCode(String levelCode) {
        LambdaQueryWrapper<CustomerLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerLevel::getLevelCode, levelCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        CustomerLevel level = getById(id);
        if (level == null) {
            throw new BusinessException("客户等级不存在");
        }
        level.setStatus(status);
        return updateById(level);
    }

    @Override
    public boolean checkCodeExists(String levelCode, Long excludeId) {
        LambdaQueryWrapper<CustomerLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerLevel::getLevelCode, levelCode);
        if (excludeId != null) {
            wrapper.ne(CustomerLevel::getId, excludeId);
        }
        return count(wrapper) > 0;
    }
}
