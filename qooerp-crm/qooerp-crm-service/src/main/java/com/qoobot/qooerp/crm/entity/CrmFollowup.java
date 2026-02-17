package com.qoobot.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 跟进记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_followup")
public class CrmFollowup {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 跟进编号
     */
    private String followupNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 商机ID(可选)
     */
    private Long opportunityId;

    /**
     * 跟进类型
     */
    private String followupType;

    /**
     * 跟进内容
     */
    private String followupContent;

    /**
     * 跟进日期
     */
    private LocalDate followupDate;

    /**
     * 跟进人ID
     */
    private Long followupPersonId;

    /**
     * 跟进人姓名
     */
    private String followupPersonName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 删除标记(0-未删除,1-已删除)
     */
    private Integer deleted;
}
