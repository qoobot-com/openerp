package com.qoobot.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 客户服务实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_service")
public class CrmService {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 服务编号
     */
    private String serviceNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 服务标题
     */
    private String serviceTitle;

    /**
     * 服务描述
     */
    private String description;

    /**
     * 优先级(1-低,2-中,3-高)
     */
    private Integer priority;

    /**
     * 状态(0-待处理,1-处理中,2-已完成)
     */
    private Integer status;

    /**
     * 处理人ID
     */
    private Long assigneeId;

    /**
     * 处理人姓名
     */
    private String assigneeName;

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
