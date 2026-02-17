package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产报工Mapper接口
 */
@Mapper
public interface ProductionReportMapper extends BaseMapper<ProductionReport> {

    /**
     * 分页查询生产报工
     *
     * @param page 分页对象
     * @param reportNo 报工单号
     * @param status 状态
     * @param workerId 工人ID
     * @param tenantId 租户ID
     * @return 分页结果
     */
    IPage<ProductionReport> selectPageByCondition(
            Page<ProductionReport> page,
            @Param("reportNo") String reportNo,
            @Param("status") String status,
            @Param("workerId") Long workerId,
            @Param("tenantId") Long tenantId
    );
}
