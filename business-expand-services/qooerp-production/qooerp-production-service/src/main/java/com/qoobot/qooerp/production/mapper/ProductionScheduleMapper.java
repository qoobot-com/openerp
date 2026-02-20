package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产排程Mapper接口
 */
@Mapper
public interface ProductionScheduleMapper extends BaseMapper<ProductionSchedule> {

    /**
     * 分页查询生产排程
     */
    IPage<ProductionSchedule> selectPageByCondition(
            Page<ProductionSchedule> page,
            @Param("scheduleCode") String scheduleCode,
            @Param("status") String status,
            @Param("equipmentId") Long equipmentId,
            @Param("tenantId") Long tenantId
    );
}
