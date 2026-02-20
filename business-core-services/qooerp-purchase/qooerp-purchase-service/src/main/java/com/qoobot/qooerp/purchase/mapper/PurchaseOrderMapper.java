package com.qoobot.qooerp.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.purchase.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {

    IPage<PurchaseOrder> queryPage(Page<PurchaseOrder> page,
                                      @Param("orderCode") String orderCode,
                                      @Param("supplierId") Long supplierId,
                                      @Param("orderStatus") String orderStatus,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
}
