package com.qoobot.qooerp.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.monitor.entity.MonitorMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface MonitorMetricsMapper extends BaseMapper<MonitorMetrics> {

    /**
     * 根据服务名称和时间范围查询指标数据
     *
     * @param serviceName 服务名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标数据列表
     */
    @Select("""
        SELECT * FROM monitor_metrics
        WHERE service_name = #{serviceName}
          AND time >= #{startTime}
          AND time <= #{endTime}
        ORDER BY time DESC
        LIMIT 1000
    """)
    List<MonitorMetrics> selectByServiceNameAndTimeRange(
        @Param("serviceName") String serviceName,
        @Param("startTime") Timestamp startTime,
        @Param("endTime") Timestamp endTime
    );

    /**
     * 根据指标名称和时间范围查询指标数据
     *
     * @param metricName 指标名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标数据列表
     */
    @Select("""
        SELECT * FROM monitor_metrics
        WHERE metric_name = #{metricName}
          AND time >= #{startTime}
          AND time <= #{endTime}
        ORDER BY time DESC
        LIMIT 1000
    """)
    List<MonitorMetrics> selectByMetricNameAndTimeRange(
        @Param("metricName") String metricName,
        @Param("startTime") Timestamp startTime,
        @Param("endTime") Timestamp endTime
    );

    /**
     * 查询最新指标值
     *
     * @param serviceName 服务名称
     * @param metricName 指标名称
     * @return 最新指标值
     */
    @Select("""
        SELECT * FROM monitor_metrics
        WHERE service_name = #{serviceName}
          AND metric_name = #{metricName}
        ORDER BY time DESC
        LIMIT 1
    """)
    MonitorMetrics selectLatestMetric(
        @Param("serviceName") String serviceName,
        @Param("metricName") String metricName
    );
}
