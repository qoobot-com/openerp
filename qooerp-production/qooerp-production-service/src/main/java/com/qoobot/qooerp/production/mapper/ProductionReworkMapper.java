package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionRework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产返工Mapper接口
 */
@Mapper
public interface ProductionReworkMapper extends BaseMapper<ProductionRework> {

    /**
     * 分页查询生产返工
     */
    IPage<ProductionRework> selectPageByCondition(
            Page<ProductionRework> page,
            @Param("reworkNo") String reworkNo,
            @Param("status") String status,
            @Param("tenantId") Long tenantId
    );
}
