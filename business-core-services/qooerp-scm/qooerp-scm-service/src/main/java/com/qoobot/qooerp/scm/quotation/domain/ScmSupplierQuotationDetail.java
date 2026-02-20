package com.qoobot.qooerp.scm.quotation.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 供应商报价明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_supplier_quotation_detail")
public class ScmSupplierQuotationDetail extends BaseEntity {
    
    /** 报价单ID */
    private Long quotationId;
    
    /** 报价单号 */
    private String quotationCode;
    
    /** 物料编码 */
    private String materialCode;
    
    /** 物料名称 */
    private String materialName;
    
    /** 规格型号 */
    private String specification;
    
    /** 单位 */
    private String unit;
    
    /** 数量 */
    private BigDecimal quantity;
    
    /** 单价 */
    private BigDecimal unitPrice;
    
    /** 金额 */
    private BigDecimal amount;
    
    /** 交货期（天） */
    private Integer deliveryDays;
}
