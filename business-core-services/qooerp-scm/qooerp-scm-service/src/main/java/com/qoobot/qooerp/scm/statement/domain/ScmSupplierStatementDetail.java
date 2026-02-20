package com.qoobot.qooerp.scm.statement.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 供应商对账明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_supplier_statement_detail")
public class ScmSupplierStatementDetail extends BaseEntity {
    
    /** 对账单ID */
    private Long statementId;
    
    /** 对账单号 */
    private String statementCode;
    
    /** 单据类型：PURCHASE_ORDER-采购订单,RECEIPT-入库单,RETURN-退货单 */
    private String orderType;
    
    /** 单据ID */
    private Long orderId;
    
    /** 单据号 */
    private String orderCode;
    
    /** 物料编码 */
    private String materialCode;
    
    /** 物料名称 */
    private String materialName;
    
    /** 数量 */
    private BigDecimal quantity;
    
    /** 单价 */
    private BigDecimal unitPrice;
    
    /** 金额 */
    private BigDecimal amount;
    
    /** 单据日期 */
    private LocalDate orderDate;
}
