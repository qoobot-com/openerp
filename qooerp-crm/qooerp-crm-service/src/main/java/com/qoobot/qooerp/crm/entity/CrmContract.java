package com.qoobot.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_contract")
public class CrmContract {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 商机ID
     */
    private Long opportunityId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 合同金额
     */
    private BigDecimal contractAmount;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 状态(0-草稿,1-已审核,2-执行中,3-已完成)
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
