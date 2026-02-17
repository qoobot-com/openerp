package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产计划Mapper接口
 */
@Mapper
public interface ProductionPlanMapper extends BaseMapper<ProductionPlan> {

    /**
     * 分页查询生产计划
     *
     * @param page 分页对象
     * @param planNo 计划编号
     * @param planName 计划名称
     * @param status 状态
     * @param tenantId 租户ID
     * @return 分页结果
     */
    IPage<ProductionPlan> selectPageByCondition(
            Page<ProductionPlan> page,
            @Param("planNo") String planNo,
            @Param("planName") String planName,
            @Param("status") String status,
            @Param("tenantId") Long tenantId
    );
}
