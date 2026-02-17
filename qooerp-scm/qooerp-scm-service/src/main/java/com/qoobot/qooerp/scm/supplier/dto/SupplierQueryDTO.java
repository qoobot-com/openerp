package com.qoobot.qooerp.scm.supplier.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 供应商查询DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class SupplierQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商编码（模糊查询）
     */
    private String supplierCode;

    /**
     * 供应商名称（模糊查询）
     */
    private String supplierName;

    /**
     * 供应商类型
     */
    private String supplierType;

    /**
     * 信用等级
     */
    private String creditRating;

    /**
     * 状态
     */
    private String status;

    /**
     * 联系人（模糊查询）
     */
    private String contactPerson;

    /**
     * 联系电话（模糊查询）
     */
    private String contactPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
