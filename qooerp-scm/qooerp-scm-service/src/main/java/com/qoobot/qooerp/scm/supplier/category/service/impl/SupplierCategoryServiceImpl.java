package com.qoobot.qooerp.scm.supplier.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.supplier.category.domain.SupplierCategory;
import com.qoobot.qooerp.scm.supplier.category.dto.SupplierCategoryDTO;
import com.qoobot.qooerp.scm.supplier.category.dto.SupplierCategoryQueryDTO;
import com.qoobot.qooerp.scm.supplier.category.dto.SupplierCategoryTreeDTO;
import com.qoobot.qooerp.scm.supplier.category.mapper.SupplierCategoryMapper;
import com.qoobot.qooerp.scm.supplier.category.service.ISupplierCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应商分类Service实现
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Service
public class SupplierCategoryServiceImpl extends ServiceImpl<SupplierCategoryMapper, SupplierCategory>
        implements ISupplierCategoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SupplierCategoryDTO dto) {
        // 校验编码唯一性
        if (checkCodeExists(dto.getCategoryCode(), null)) {
            throw new BusinessException("供应商分类编码已存在：" + dto.getCategoryCode());
        }

        // 计算分类层级
        if (dto.getParentId() == null) {
            dto.setParentId(0L);
            dto.setLevel(1);
        } else {
            SupplierCategory parent = getById(dto.getParentId());
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

        SupplierCategory category = new SupplierCategory();
        BeanUtils.copyProperties(dto, category);

        save(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SupplierCategoryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }

        SupplierCategory category = getById(dto.getId());
        if (category == null) {
            throw new BusinessException("供应商分类不存在");
        }

        // 如果修改了编码，需要校验唯一性
        if (StringUtils.hasText(dto.getCategoryCode()) &&
            !dto.getCategoryCode().equals(category.getCategoryCode())) {
            if (checkCodeExists(dto.getCategoryCode(), dto.getId())) {
                throw new BusinessException("供应商分类编码已存在：" + dto.getCategoryCode());
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
                SupplierCategory parent = getById(dto.getParentId());
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
        SupplierCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("供应商分类不存在");
        }

        // 检查是否有子分类
        if (hasChildren(id)) {
            throw new BusinessException("该分类下存在子分类，不能删除");
        }

        // TODO: 检查是否有关联的供应商
        // if (hasRelatedSuppliers(id)) {
        //     throw new BusinessException("该分类下存在关联的供应商，不能删除");
        // }

        return removeById(id);
    }

    @Override
    public SupplierCategoryDTO getDetail(Long id) {
        SupplierCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("供应商分类不存在");
        }

        SupplierCategoryDTO dto = new SupplierCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }

    @Override
    public PageResult<SupplierCategory> queryPage(SupplierCategoryQueryDTO queryDTO) {
        LambdaQueryWrapper<SupplierCategory> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getCategoryCode())) {
            wrapper.like(SupplierCategory::getCategoryCode, queryDTO.getCategoryCode());
        }
        if (StringUtils.hasText(queryDTO.getCategoryName())) {
            wrapper.like(SupplierCategory::getCategoryName, queryDTO.getCategoryName());
        }
        if (queryDTO.getParentId() != null) {
            wrapper.eq(SupplierCategory::getParentId, queryDTO.getParentId());
        }
        if (queryDTO.getLevel() != null) {
            wrapper.eq(SupplierCategory::getLevel, queryDTO.getLevel());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(SupplierCategory::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByAsc(SupplierCategory::getSortOrder)
               .orderByDesc(SupplierCategory::getCreateTime);

        Page<SupplierCategory> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        Page<SupplierCategory> result = page(page, wrapper);

        return new PageResult<>(page.getCurrent(), page.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public List<SupplierCategoryTreeDTO> getCategoryTree() {
        // 查询所有启用的分类
        LambdaQueryWrapper<SupplierCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierCategory::getStatus, "ENABLED");
        wrapper.orderByAsc(SupplierCategory::getSortOrder)
               .orderByAsc(SupplierCategory::getCategoryCode);

        List<SupplierCategory> allCategories = list(wrapper);
        return buildCategoryTree(allCategories, 0L);
    }

    /**
     * 递归构建分类树
     */
    private List<SupplierCategoryTreeDTO> buildCategoryTree(List<SupplierCategory> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(category -> category.getParentId().equals(parentId))
                .map(category -> {
                    SupplierCategoryTreeDTO treeDTO = new SupplierCategoryTreeDTO();
                    BeanUtils.copyProperties(category, treeDTO);
                    // 递归构建子分类
                    List<SupplierCategoryTreeDTO> children = buildCategoryTree(allCategories, category.getId());
                    treeDTO.setChildren(children);
                    return treeDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierCategory> getChildrenByParentId(Long parentId) {
        LambdaQueryWrapper<SupplierCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierCategory::getParentId, parentId);
        wrapper.eq(SupplierCategory::getStatus, "ENABLED");
        wrapper.orderByAsc(SupplierCategory::getSortOrder);
        return list(wrapper);
    }

    @Override
    public SupplierCategory getByCode(String categoryCode) {
        LambdaQueryWrapper<SupplierCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierCategory::getCategoryCode, categoryCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        SupplierCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("供应商分类不存在");
        }
        category.setStatus(status);
        return updateById(category);
    }

    @Override
    public boolean checkCodeExists(String categoryCode, Long excludeId) {
        LambdaQueryWrapper<SupplierCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierCategory::getCategoryCode, categoryCode);
        if (excludeId != null) {
            wrapper.ne(SupplierCategory::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    @Override
    public boolean hasChildren(Long parentId) {
        LambdaQueryWrapper<SupplierCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierCategory::getParentId, parentId);
        return count(wrapper) > 0;
    }

    /**
     * 判断是否是子分类
     */
    private boolean isChildCategory(Long parentId, Long childId) {
        if (parentId.equals(childId)) {
            return true;
        }
        SupplierCategory child = getById(childId);
        if (child == null || child.getParentId() == null || child.getParentId() == 0) {
            return false;
        }
        return isChildCategory(parentId, child.getParentId());
    }
}
