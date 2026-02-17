package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产领料Mapper接口
 */
@Mapper
public interface ProductionMaterialMapper extends BaseMapper<ProductionMaterial> {

    /**
     * 分页查询生产领料
     */
    IPage<ProductionMaterial> selectPageByCondition(
            Page<ProductionMaterial> page,
            @Param("materialNo") String materialNo,
            @Param("status") String status,
            @Param("tenantId") Long tenantId
    );
}
