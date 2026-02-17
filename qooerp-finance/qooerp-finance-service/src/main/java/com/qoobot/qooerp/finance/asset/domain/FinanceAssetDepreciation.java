package com.qoobot.qooerp.finance.asset.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 资产折旧实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_asset_depreciation")
public class FinanceAssetDepreciation extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 资产ID
     */
    @TableField("asset_id")
    private Long assetId;

    /**
     * 折旧期间：YYYY-MM
     */
    @TableField("depreciation_period")
    private String depreciationPeriod;

    /**
     * 折旧金额
     */
    @TableField("depreciation_amount")
    private BigDecimal depreciationAmount;

    /**
     * 累计折旧
     */
    @TableField("accumulated_depreciation")
    private BigDecimal accumulatedDepreciation;

    /**
     * 净值
     */
    @TableField("net_value")
    private BigDecimal netValue;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
