package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产订单Mapper接口
 */
@Mapper
public interface ProductionOrderMapper extends BaseMapper<ProductionOrder> {

    /**
     * 分页查询生产订单
     *
     * @param page 分页对象
     * @param orderNo 订单编号
     * @param status 状态
     * @param workshopId 车间ID
     * @param tenantId 租户ID
     * @return 分页结果
     */
    IPage<ProductionOrder> selectPageByCondition(
            Page<ProductionOrder> page,
            @Param("orderNo") String orderNo,
            @Param("status") String status,
            @Param("workshopId") Long workshopId,
            @Param("tenantId") Long tenantId
    );
}
