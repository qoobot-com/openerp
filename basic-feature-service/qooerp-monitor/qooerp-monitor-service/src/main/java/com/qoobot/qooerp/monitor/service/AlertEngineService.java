package com.qoobot.qooerp.monitor.service;

import com.qoobot.qooerp.monitor.dto.AlertRuleCreate;
import com.qoobot.qooerp.monitor.dto.AlertRuleQuery;
import com.qoobot.qooerp.monitor.dto.AlertRuleUpdate;
import com.qoobot.qooerp.monitor.dto.AlertInstanceQuery;
import com.qoobot.qooerp.monitor.entity.AlertRule;
import com.qoobot.qooerp.monitor.entity.AlertInstance;
import com.qoobot.qooerp.monitor.entity.AlertHistory;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 告警引擎服务接口
 */
public interface AlertEngineService {

    /**
     * 创建告警规则
     *
     * @param dto 告警规则创建 DTO
     * @return 创建的告警规则
     */
    AlertRule createRule(AlertRuleCreate dto);

    /**
     * 更新告警规则
     *
     * @param id  规则 ID
     * @param dto 告警规则更新 DTO
     * @return 更新后的告警规则
     */
    AlertRule updateRule(Long id, AlertRuleUpdate dto);

    /**
     * 删除告警规则
     *
     * @param id 规则 ID
     */
    void deleteRule(Long id);

    /**
     * 获取告警规则
     *
     * @param id 规则 ID
     * @return 告警规则
     */
    AlertRule getRule(Long id);

    /**
     * 查询告警规则列表
     *
     * @param query 查询条件
     * @return 告警规则列表
     */
    Map<String, Object> listRules(AlertRuleQuery query);

    /**
     * 启用/禁用告警规则
     *
     * @param id      规则 ID
     * @param enabled 是否启用
     */
    void toggleRule(Long id, Boolean enabled);

    /**
     * 手动触发规则检查
     *
     * @param ruleId 规则 ID
     */
    void checkRule(Long ruleId);

    /**
     * 批量触发所有规则检查
     */
    void checkAllRules();

    /**
     * 查询告警实例列表
     *
     * @param query 查询条件
     * @return 告警实例列表
     */
    Map<String, Object> listInstances(AlertInstanceQuery query);

    /**
     * 获取告警实例
     *
     * @param id 实例 ID
     * @return 告警实例
     */
    AlertInstance getInstance(Long id);

    /**
     * 确认告警
     *
     * @param id     实例 ID
     * @param reason 确认原因
     */
    void acknowledgeInstance(Long id, String reason);

    /**
     * 解决告警
     *
     * @param id     实例 ID
     * @param reason 解决原因
     */
    void resolveInstance(Long id, String reason);

    /**
     * 查询告警历史
     *
     * @param instanceId 实例 ID
     * @return 告警历史列表
     */
    List<AlertHistory> listHistory(Long instanceId);

    /**
     * 获取告警统计
     *
     * @return 统计信息
     */
    Map<String, Object> getStatistics();

    /**
     * 设置规则静默
     *
     * @param id         规则 ID
     * @param silenceStart 静默开始时间
     * @param silenceEnd   静默结束时间
     */
    void setSilence(Long id, Timestamp silenceStart, Timestamp silenceEnd);

    /**
     * 取消规则静默
     *
     * @param id 规则 ID
     */
    void cancelSilence(Long id);

    /**
     * 重新通知告警
     *
     * @param id 实例 ID
     */
    void renotifyInstance(Long id);
}
