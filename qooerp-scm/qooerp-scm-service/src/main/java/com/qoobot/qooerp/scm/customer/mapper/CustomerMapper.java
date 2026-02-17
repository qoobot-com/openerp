package com.qoobot.qooerp.scm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.scm.customer.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户Mapper接口
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
