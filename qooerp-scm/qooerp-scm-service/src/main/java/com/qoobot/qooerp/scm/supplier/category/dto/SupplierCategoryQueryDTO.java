package com.qoobot.qooerp.scm.supplier.category.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 供应商分类查询DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class SupplierCategoryQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 分类编码（模糊查询）
     */
    private String categoryCode;

    /**
     * 分类名称（模糊查询）
     */
    private String categoryName;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 状态（启用/禁用）
     */
    private String status;
}
