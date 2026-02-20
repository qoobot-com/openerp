package com.qoobot.qooerp.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商机实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_opportunity")
public class CrmOpportunity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商机编号
     */
    private String opportunityNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 商机名称
     */
    private String opportunityName;

    /**
     * 商机阶段(1-初步接触,2-需求确认,3-方案报价,4-商务谈判,5-合同签订)
     */
    private Integer stage;

    /**
     * 预计金额
     */
    private BigDecimal amount;

    /**
     * 成功概率(%)
     */
    private Integer probability;

    /**
     * 预计成交日期
     */
    private LocalDate expectedCloseDate;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 负责人姓名
     */
    private String ownerName;

    /**
     * 状态(0-进行中,1-已转化,2-已关闭)
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
