package com.qoobot.qooerp.scm.quotation.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商报价实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_supplier_quotation")
public class ScmSupplierQuotation extends BaseEntity {
    
    /** 报价单号 */
    private String quotationCode;
    
    /** 供应商ID */
    private Long supplierId;
    
    /** 供应商编码 */
    private String supplierCode;
    
    /** 供应商名称 */
    private String supplierName;
    
    /** 报价日期 */
    private LocalDate quotationDate;
    
    /** 报价有效期 */
    private LocalDate validUntil;
    
    /** 报价类型：NORMAL-正常报价,URGENT-紧急报价 */
    private String quotationType;
    
    /** 报价总金额 */
    private BigDecimal totalAmount;
    
    /** 审核状态：PENDING-待审核,APPROVED-已通过,REJECTED-已拒绝 */
    private String auditStatus;
    
    /** 审核人 */
    private String auditor;
    
    /** 审核时间 */
    private LocalDateTime auditTime;
    
    /** 审核意见 */
    private String auditRemark;
    
    /** 是否转采购订单：0-未转换,1-已转换 */
    private Integer converted;
    
    /** 采购订单ID */
    private Long purchaseOrderId;
    
    /** 采购订单号 */
    private String purchaseOrderCode;
    
    /** 转换时间 */
    private LocalDateTime convertTime;
    
    /** 附件地址 */
    private String attachmentUrl;
}
