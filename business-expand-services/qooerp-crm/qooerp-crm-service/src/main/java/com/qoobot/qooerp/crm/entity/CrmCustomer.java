package com.qoobot.qooerp.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_customer")
public class CrmCustomer {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户编号
     */
    private String customerNo;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户类型(1-企业,2-个人)
     */
    private Integer customerType;

    /**
     * 客户等级(1-A级,2-B级,3-C级)
     */
    private Integer level;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 所在地区
     */
    private String region;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 地址
     */
    private String address;

    /**
     * 状态(0-待审核,1-已审核,2-已停用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 删除标记(0-未删除,1-已删除)
     */
    private Integer deleted;
}
