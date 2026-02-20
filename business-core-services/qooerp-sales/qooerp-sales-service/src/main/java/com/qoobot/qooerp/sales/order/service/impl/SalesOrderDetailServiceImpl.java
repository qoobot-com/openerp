package com.qoobot.qooerp.sales.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.sales.order.domain.SalesOrderDetail;
import com.qoobot.qooerp.sales.order.mapper.SalesOrderDetailMapper;
import com.qoobot.qooerp.sales.order.service.SalesOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * 销售订单明细服务实现
 */
@Service
public class SalesOrderDetailServiceImpl extends ServiceImpl<SalesOrderDetailMapper, SalesOrderDetail>
        implements SalesOrderDetailService {
}
