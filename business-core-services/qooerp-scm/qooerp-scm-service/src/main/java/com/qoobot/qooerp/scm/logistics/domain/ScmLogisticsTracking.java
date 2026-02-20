package com.qoobot.qooerp.scm.logistics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 物流跟踪实体
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_logistics_tracking")
public class ScmLogisticsTracking extends BaseEntity {

    /**
     * 物流ID
     */
    private Long logisticsId;

    /**
     * 物流单号
     */
    private String logisticsCode;

    /**
     * 跟踪时间
     */
    private LocalDateTime trackingTime;

    /**
     * 位置
     */
    private String location;

    /**
     * 状态
     */
    private String status;

    /**
     * 描述
     */
    private String description;

    /**
     * 操作人
     */
    private String operator;
}
