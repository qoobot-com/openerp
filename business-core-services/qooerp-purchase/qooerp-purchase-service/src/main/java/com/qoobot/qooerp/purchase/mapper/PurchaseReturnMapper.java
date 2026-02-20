package com.qoobot.qooerp.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.purchase.entity.PurchaseReturn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface PurchaseReturnMapper extends BaseMapper<PurchaseReturn> {

    IPage<PurchaseReturn> queryPage(Page<PurchaseReturn> page,
                                      @Param("returnCode") String returnCode,
                                      @Param("orderId") Long orderId,
                                      @Param("supplierId") Long supplierId,
                                      @Param("returnStatus") String returnStatus,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
}
