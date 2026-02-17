package com.qoobot.qooerp.finance.period.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;

/**
 * 会计期间
 */
@Data
@TableName("finance_period")
public class FinancePeriod extends BaseEntity {

    /**
     * 期间编码
     */
    private String periodCode;

    /**
     * 会计年度
     */
    private Integer fiscalYear;

    /**
     * 会计期间（1-12）
     */
    private Integer periodNo;

    /**
     * 开始日期
     */
    private java.time.LocalDate startDate;

    /**
     * 结束日期
     */
    private java.time.LocalDate endDate;

    /**
     * 期间状态（0-未开始 1-进行中 2-已结账）
     */
    private Integer status;

    /**
     * 是否允许录入凭证（0-不允许 1-允许）
     */
    private Integer allowEntry;

    /**
     * 结账人
     */
    private String closerId;

    /**
     * 结账人姓名
     */
    private String closerName;

    /**
     * 结账时间
     */
    private java.time.LocalDateTime closeTime;

    /**
     * 反结账次数
     */
    private Integer reopenCount;

    /**
     * 备注
     */
    private String remark;
}
