package com.qoobot.qooerp.scm.customer.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户查询DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class CustomerQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户编码（模糊查询）
     */
    private String customerCode;

    /**
     * 客户名称（模糊查询）
     */
    private String customerName;

    /**
     * 客户类型
     */
    private String customerType;

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
