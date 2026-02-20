package com.qoobot.qooerp.finance.asset.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 固定资产实体
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_asset")
public class FinanceAsset extends BaseEntity {

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
     * 资产编码
     */
    @TableField("asset_code")
    private String assetCode;

    /**
     * 资产名称
     */
    @TableField("asset_name")
    private String assetName;

    /**
     * 资产分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 资产分类名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 规格型号
     */
    @TableField("specification")
    private String specification;

    /**
     * 计量单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 数量
     */
    @TableField("quantity")
    private BigDecimal quantity;

    /**
     * 原值
     */
    @TableField("original_value")
    private BigDecimal originalValue;

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
     * 使用部门ID
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 使用部门名称
     */
    @TableField("department_name")
    private String departmentName;

    /**
     * 使用人ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 使用人姓名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 存放地点
     */
    @TableField("location")
    private String location;

    /**
     * 入账日期
     */
    @TableField("entry_date")
    private LocalDate entryDate;

    /**
     * 开始使用日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 使用年限（年）
     */
    @TableField("useful_life")
    private Integer usefulLife;

    /**
     * 折旧方法：STRAIGHT_LINE-直线法，DOUBLE_DECLINING-双倍余额递减法，SUM_OF_YEARS-年数总和法
     */
    @TableField("depreciation_method")
    private String depreciationMethod;

    /**
     * 折旧状态：NORMAL-正常，SUSPENDED-暂停，COMPLETED-已折旧完毕
     */
    @TableField("depreciation_status")
    private String depreciationStatus;

    /**
     * 资产状态：NORMAL-正常，SCRAPPED-已报废，TRANSFERRED-已调拨，LOST-丢失
     */
    @TableField("status")
    private String status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 版本号
     */
    @Version
    @TableField("version")
    private Integer version;
}
