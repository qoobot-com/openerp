package com.qoobot.qooerp.scm.customer.category.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户分类DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class CustomerCategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类名称
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
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态（启用/禁用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
