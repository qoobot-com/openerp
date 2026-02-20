package com.qoobot.qooerp.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.purchase.entity.PurchaseReceipt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface PurchaseReceiptMapper extends BaseMapper<PurchaseReceipt> {

    IPage<PurchaseReceipt> queryPage(Page<PurchaseReceipt> page,
                                       @Param("receiptCode") String receiptCode,
                                       @Param("orderId") Long orderId,
                                       @Param("supplierId") Long supplierId,
                                       @Param("receiptStatus") String receiptStatus,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
}
