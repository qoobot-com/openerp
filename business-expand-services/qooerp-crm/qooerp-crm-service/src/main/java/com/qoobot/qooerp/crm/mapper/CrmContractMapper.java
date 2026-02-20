package com.qoobot.qooerp.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.crm.entity.CrmContract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 合同Mapper
 */
@Mapper
public interface CrmContractMapper extends BaseMapper<CrmContract> {

    /**
     * 分页查询合同
     */
    IPage<CrmContract> selectPageWithCondition(Page<CrmContract> page,
                                               @Param("customerId") Long customerId,
                                               @Param("status") Integer status);
}
