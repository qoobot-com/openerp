package com.qoobot.qooerp.sales.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.sales.order.domain.SalesOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售订单 Mapper
 */
@Mapper
public interface SalesOrderMapper extends BaseMapper<SalesOrder> {
}
