package com.qoobot.qooerp.scm.supplier.category.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.supplier.category.domain.SupplierCategory;
import com.qoobot.qooerp.scm.supplier.category.dto.SupplierCategoryDTO;
import com.qoobot.qooerp.scm.supplier.category.dto.SupplierCategoryQueryDTO;
import com.qoobot.qooerp.scm.supplier.category.dto.SupplierCategoryTreeDTO;

import java.util.List;

/**
 * 供应商分类Service接口
 *
 * @author QooERP
 * @since 2026-02-17
 */
public interface ISupplierCategoryService {

    /**
     * 创建供应商分类
     *
     * @param dto 供应商分类DTO
     * @return 分类ID
     */
    Long create(SupplierCategoryDTO dto);

    /**
     * 更新供应商分类
     *
     * @param dto 供应商分类DTO
     * @return 是否成功
     */
    boolean update(SupplierCategoryDTO dto);

    /**
     * 删除供应商分类
     *
     * @param id 分类ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类DTO
     */
    SupplierCategoryDTO getDetail(Long id);

    /**
     * 分页查询分类列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<SupplierCategory> queryPage(SupplierCategoryQueryDTO queryDTO);

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    List<SupplierCategoryTreeDTO> getCategoryTree();

    /**
     * 根据父分类ID获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<SupplierCategory> getChildrenByParentId(Long parentId);

    /**
     * 根据编码获取分类
     *
     * @param categoryCode 分类编码
     * @return 分类实体
     */
    SupplierCategory getByCode(String categoryCode);

    /**
     * 更新分类状态
     *
     * @param id 分类ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, String status);

    /**
     * 校验分类编码是否存在
     *
     * @param categoryCode 分类编码
     * @param excludeId 排除的分类ID（更新时使用）
     * @return 是否存在
     */
    boolean checkCodeExists(String categoryCode, Long excludeId);

    /**
     * 校验是否有子分类
     *
     * @param parentId 父分类ID
     * @return 是否有子分类
     */
    boolean hasChildren(Long parentId);
}
