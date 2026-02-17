package com.qoobot.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.crm.entity.CrmOpportunity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商机Mapper
 */
@Mapper
public interface CrmOpportunityMapper extends BaseMapper<CrmOpportunity> {

    /**
     * 分页查询商机
     */
    IPage<CrmOpportunity> selectPageWithCondition(Page<CrmOpportunity> page,
                                                  @Param("customerId") Long customerId,
                                                  @Param("stage") Integer stage,
                                                  @Param("status") Integer status,
                                                  @Param("ownerId") Long ownerId);
}
