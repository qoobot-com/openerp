package com.qoobot.qooerp.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.crm.entity.CrmCustomer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客户Mapper
 */
@Mapper
public interface CrmCustomerMapper extends BaseMapper<CrmCustomer> {

    /**
     * 分页查询客户
     */
    IPage<CrmCustomer> selectPageWithCondition(Page<CrmCustomer> page,
                                                @Param("customerName") String customerName,
                                                @Param("customerType") Integer customerType,
                                                @Param("level") Integer level,
                                                @Param("status") Integer status);
}
