package com.qoobot.qooerp.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.monitor.entity.AlertInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface AlertInstanceMapper extends BaseMapper<AlertInstance> {

    /**
     * 查询告警中的实例
     *
     * @return 告警中的实例列表
     */
    @Select("""
        SELECT * FROM alert_instance
        WHERE status IN ('PENDING', 'FIRING')
          AND deleted = 0
    """)
    List<AlertInstance> selectFiringInstances();

    /**
     * 查询待检查的告警实例
     *
     * @param checkInterval 检查间隔（秒）
     * @return 待检查的实例列表
     */
    @Select("""
        SELECT * FROM alert_instance
        WHERE status = 'FIRING'
          AND (last_notify_time IS NULL
               OR EXTRACT(EPOCH FROM (NOW() - last_notify_time)) >= #{checkInterval})
          AND deleted = 0
    """)
    List<AlertInstance> selectPendingNotify(@Param("checkInterval") long checkInterval);

    /**
     * 根据规则和服务查询最近的告警实例
     *
     * @param ruleId       规则 ID
     * @param serviceName  服务名称
     * @param instance     实例标识
     * @return 告警实例
     */
    @Select("""
        SELECT * FROM alert_instance
        WHERE rule_id = #{ruleId}
          AND service_name = #{serviceName}
          AND instance = #{instance}
          AND status IN ('PENDING', 'FIRING')
          AND deleted = 0
        ORDER BY firing_time DESC
        LIMIT 1
    """)
    AlertInstance selectByRuleAndService(
        @Param("ruleId") Long ruleId,
        @Param("serviceName") String serviceName,
        @Param("instance") String instance
    );

    /**
     * 更新告警实例状态
     *
     * @param id          实例 ID
     * @param status      状态
     * @param resolvedTime 解决时间
     * @return 影响行数
     */
    @Update("""
        UPDATE alert_instance
        SET status = #{status},
            resolved_time = #{resolvedTime},
            updated_time = NOW()
        WHERE id = #{id}
    """)
    int updateStatus(
        @Param("id") Long id,
        @Param("status") String status,
        @Param("resolvedTime") Timestamp resolvedTime
    );

    /**
     * 更新通知次数
     *
     * @param id 实例 ID
     * @return 影响行数
     */
    @Update("""
        UPDATE alert_instance
        SET notify_count = notify_count + 1,
            last_notify_time = NOW(),
            updated_time = NOW()
        WHERE id = #{id}
    """)
    int incrementNotifyCount(@Param("id") Long id);
}
