package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionQuality;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产质检Mapper接口
 */
@Mapper
public interface ProductionQualityMapper extends BaseMapper<ProductionQuality> {

    /**
     * 分页查询生产质检
     */
    IPage<ProductionQuality> selectPageByCondition(
            Page<ProductionQuality> page,
            @Param("qualityNo") String qualityNo,
            @Param("status") String status,
            @Param("tenantId") Long tenantId
    );
}
