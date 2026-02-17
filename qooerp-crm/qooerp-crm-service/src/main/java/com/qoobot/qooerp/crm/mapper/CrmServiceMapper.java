package com.qoobot.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.crm.entity.CrmService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客户服务Mapper
 */
@Mapper
public interface CrmServiceMapper extends BaseMapper<CrmService> {

    /**
     * 分页查询服务
     */
    IPage<CrmService> selectPageWithCondition(Page<CrmService> page,
                                               @Param("customerId") Long customerId,
                                               @Param("status") Integer status,
                                               @Param("assigneeId") Long assigneeId);
}
