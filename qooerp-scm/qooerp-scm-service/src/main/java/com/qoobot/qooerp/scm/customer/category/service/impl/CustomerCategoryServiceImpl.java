package com.qoobot.qooerp.scm.customer.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.customer.category.domain.CustomerCategory;
import com.qoobot.qooerp.scm.customer.category.dto.CustomerCategoryDTO;
import com.qoobot.qooerp.scm.customer.category.dto.CustomerCategoryQueryDTO;
import com.qoobot.qooerp.scm.customer.category.dto.CustomerCategoryTreeDTO;
import com.qoobot.qooerp.scm.customer.category.mapper.CustomerCategoryMapper;
import com.qoobot.qooerp.scm.customer.category.service.ICustomerCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户分类Service实现
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Service
public class CustomerCategoryServiceImpl extends ServiceImpl<CustomerCategoryMapper, CustomerCategory>
        implements ICustomerCategoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CustomerCategoryDTO dto) {
        // 校验编码唯一性
        if (checkCodeExists(dto.getCategoryCode(), null)) {
            throw new BusinessException("客户分类编码已存在：" + dto.getCategoryCode());
        }

        // 计算分类层级
        if (dto.getParentId() == null) {
            dto.setParentId(0L);
            dto.setLevel(1);
        } else {
            CustomerCategory parent = getById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
            dto.setLevel(parent.getLevel() + 1);
        }

        // 设置默认排序
        if (dto.getSortOrder() == null) {
            dto.setSortOrder(0);
        }

        // 设置默认状态
        if (!StringUtils.hasText(dto.getStatus())) {
            dto.setStatus("ENABLED");
        }

        CustomerCategory category = new CustomerCategory();
        BeanUtils.copyProperties(dto, category);

        save(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(CustomerCategoryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }

        CustomerCategory category = getById(dto.getId());
        if (category == null) {
            throw new BusinessException("客户分类不存在");
        }

        // 如果修改了编码，需要校验唯一性
        if (StringUtils.hasText(dto.getCategoryCode()) &&
            !dto.getCategoryCode().equals(category.getCategoryCode())) {
            if (checkCodeExists(dto.getCategoryCode(), dto.getId())) {
                throw new BusinessException("客户分类编码已存在：" + dto.getCategoryCode());
            }
        }

        // 如果修改了父分类，需要校验并重新计算层级
        if (dto.getParentId() != null && !dto.getParentId().equals(category.getParentId())) {
            // 不能将分类设置为自己的子分类
            if (isChildCategory(dto.getId(), dto.getParentId())) {
                throw new BusinessException("不能将分类设置为自己的子分类");
            }

            if (dto.getParentId() == 0) {
                dto.setLevel(1);
            } else {
                CustomerCategory parent = getById(dto.getParentId());
                if (parent == null) {
                    throw new BusinessException("父分类不存在");
                }
                dto.setLevel(parent.getLevel() + 1);
            }
        }

        BeanUtils.copyProperties(dto, category);
        return updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        CustomerCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("客户分类不存在");
        }

        // 检查是否有子分类
        if (hasChildren(id)) {
            throw new BusinessException("该分类下存在子分类，不能删除");
        }

        // TODO: 检查是否有关联的客户
        // if (hasRelatedCustomers(id)) {
        //     throw new BusinessException("该分类下存在关联的客户，不能删除");
        // }

        return removeById(id);
    }

    @Override
    public CustomerCategoryDTO getDetail(Long id) {
        CustomerCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("客户分类不存在");
        }

        CustomerCategoryDTO dto = new CustomerCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }

    @Override
    public PageResult<CustomerCategory> queryPage(CustomerCategoryQueryDTO queryDTO) {
        LambdaQueryWrapper<CustomerCategory> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getCategoryCode())) {
            wrapper.like(CustomerCategory::getCategoryCode, queryDTO.getCategoryCode());
        }
        if (StringUtils.hasText(queryDTO.getCategoryName())) {
            wrapper.like(CustomerCategory::getCategoryName, queryDTO.getCategoryName());
        }
        if (queryDTO.getParentId() != null) {
            wrapper.eq(CustomerCategory::getParentId, queryDTO.getParentId());
        }
        if (queryDTO.getLevel() != null) {
            wrapper.eq(CustomerCategory::getLevel, queryDTO.getLevel());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(CustomerCategory::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByAsc(CustomerCategory::getSortOrder)
               .orderByDesc(CustomerCategory::getCreateTime);

        Page<CustomerCategory> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        Page<CustomerCategory> result = page(page, wrapper);

        return new PageResult<>(page.getCurrent(), page.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public List<CustomerCategoryTreeDTO> getCategoryTree() {
        // 查询所有启用的分类
        LambdaQueryWrapper<CustomerCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerCategory::getStatus, "ENABLED");
        wrapper.orderByAsc(CustomerCategory::getSortOrder)
               .orderByAsc(CustomerCategory::getCategoryCode);

        List<CustomerCategory> allCategories = list(wrapper);
        return buildCategoryTree(allCategories, 0L);
    }

    /**
     * 递归构建分类树
     */
    private List<CustomerCategoryTreeDTO> buildCategoryTree(List<CustomerCategory> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(category -> category.getParentId().equals(parentId))
                .map(category -> {
                    CustomerCategoryTreeDTO treeDTO = new CustomerCategoryTreeDTO();
                    BeanUtils.copyProperties(category, treeDTO);
                    // 递归构建子分类
                    List<CustomerCategoryTreeDTO> children = buildCategoryTree(allCategories, category.getId());
                    treeDTO.setChildren(children);
                    return treeDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerCategory> getChildrenByParentId(Long parentId) {
        LambdaQueryWrapper<CustomerCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerCategory::getParentId, parentId);
        wrapper.eq(CustomerCategory::getStatus, "ENABLED");
        wrapper.orderByAsc(CustomerCategory::getSortOrder);
        return list(wrapper);
    }

    @Override
    public CustomerCategory getByCode(String categoryCode) {
        LambdaQueryWrapper<CustomerCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerCategory::getCategoryCode, categoryCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        CustomerCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("客户分类不存在");
        }
        category.setStatus(status);
        return updateById(category);
    }

    @Override
    public boolean checkCodeExists(String categoryCode, Long excludeId) {
        LambdaQueryWrapper<CustomerCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerCategory::getCategoryCode, categoryCode);
        if (excludeId != null) {
            wrapper.ne(CustomerCategory::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    @Override
    public boolean hasChildren(Long parentId) {
        LambdaQueryWrapper<CustomerCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerCategory::getParentId, parentId);
        return count(wrapper) > 0;
    }

    /**
     * 判断是否是子分类
     */
    private boolean isChildCategory(Long parentId, Long childId) {
        if (parentId.equals(childId)) {
            return true;
        }
        CustomerCategory child = getById(childId);
        if (child == null || child.getParentId() == null || child.getParentId() == 0) {
            return false;
        }
        return isChildCategory(parentId, child.getParentId());
    }
}
