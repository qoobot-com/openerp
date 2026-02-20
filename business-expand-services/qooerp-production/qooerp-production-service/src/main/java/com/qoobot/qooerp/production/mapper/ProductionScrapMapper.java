package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionScrap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产报废Mapper接口
 */
@Mapper
public interface ProductionScrapMapper extends BaseMapper<ProductionScrap> {

    /**
     * 分页查询生产报废
     */
    IPage<ProductionScrap> selectPageByCondition(
            Page<ProductionScrap> page,
            @Param("scrapNo") String scrapNo,
            @Param("status") String status,
            @Param("tenantId") Long tenantId
    );
}
