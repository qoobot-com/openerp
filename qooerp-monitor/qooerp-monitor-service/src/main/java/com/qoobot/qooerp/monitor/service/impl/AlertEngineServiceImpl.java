package com.qoobot.qooerp.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qoobot.qooerp.monitor.dto.AlertRuleCreate;
import com.qoobot.qooerp.monitor.dto.AlertRuleQuery;
import com.qoobot.qooerp.monitor.dto.AlertRuleUpdate;
import com.qoobot.qooerp.monitor.dto.AlertInstanceQuery;
import com.qoobot.qooerp.monitor.entity.AlertRule;
import com.qoobot.qooerp.monitor.entity.AlertInstance;
import com.qoobot.qooerp.monitor.entity.AlertHistory;
import com.qoobot.qooerp.monitor.mapper.AlertRuleMapper;
import com.qoobot.qooerp.monitor.mapper.AlertInstanceMapper;
import com.qoobot.qooerp.monitor.mapper.AlertHistoryMapper;
import com.qoobot.qooerp.monitor.mapper.MonitorMetricsMapper;
import com.qoobot.qooerp.monitor.service.AlertEngineService;
import com.qoobot.qooerp.monitor.service.AlertNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 告警引擎服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertEngineServiceImpl extends ServiceImpl<AlertRuleMapper, AlertRule>
        implements AlertEngineService {

    private final AlertInstanceMapper alertInstanceMapper;
    private final AlertHistoryMapper alertHistoryMapper;
    private final MonitorMetricsMapper metricsMapper;
    private final AlertNotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public AlertRule createRule(AlertRuleCreate dto) {
        AlertRule rule = new AlertRule();
        BeanUtils.copyProperties(dto, rule);
        rule.setEnabled(true);
        rule.setCreatedTime(Timestamp.from(Instant.now()));
        rule.setUpdatedTime(Timestamp.from(Instant.now()));
        
        if (dto.getNotifyChannels() != null) {
            try {
                rule.setNotifyChannels(objectMapper.writeValueAsString(dto.getNotifyChannels()));
            } catch (Exception e) {
                log.error("序列化通知渠道失败", e);
            }
        }
        
        baseMapper.insert(rule);
        log.info("创建告警规则成功: {}", rule.getRuleName());
        return rule;
    }

    @Override
    @Transactional
    public AlertRule updateRule(Long id, AlertRuleUpdate dto) {
        AlertRule rule = baseMapper.selectById(id);
        if (rule == null) {
            throw new RuntimeException("告警规则不存在");
        }
        
        BeanUtils.copyProperties(dto, rule, "id", "createdTime", "createdBy");
        rule.setUpdatedTime(Timestamp.from(Instant.now()));
        
        if (dto.getNotifyChannels() != null) {
            try {
                rule.setNotifyChannels(objectMapper.writeValueAsString(dto.getNotifyChannels()));
            } catch (Exception e) {
                log.error("序列化通知渠道失败", e);
            }
        }
        
        baseMapper.updateById(rule);
        log.info("更新告警规则成功: {}", rule.getRuleName());
        return rule;
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        baseMapper.deleteById(id);
        
        // 同时解决该规则下的所有告警实例
        LambdaUpdateWrapper<AlertInstance> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AlertInstance::getRuleId, id)
               .in(AlertInstance::getStatus, Arrays.asList("PENDING", "FIRING"))
               .set(AlertInstance::getStatus, "RESOLVED")
               .set(AlertInstance::getResolvedTime, Timestamp.from(Instant.now()));
        alertInstanceMapper.update(null, wrapper);
        
        log.info("删除告警规则成功: {}", id);
    }

    @Override
    public AlertRule getRule(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public Map<String, Object> listRules(AlertRuleQuery query) {
        LambdaQueryWrapper<AlertRule> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getRuleName() != null) {
            wrapper.like(AlertRule::getRuleName, query.getRuleName());
        }
        if (query.getRuleType() != null) {
            wrapper.eq(AlertRule::getRuleType, query.getRuleType());
        }
        if (query.getSeverity() != null) {
            wrapper.eq(AlertRule::getSeverity, query.getSeverity());
        }
        if (query.getEnabled() != null) {
            wrapper.eq(AlertRule::getEnabled, query.getEnabled());
        }
        
        wrapper.orderByDesc(AlertRule::getCreatedTime);
        
        Page<AlertRule> page = new Page<>(query.getPage(), query.getSize());
        page = baseMapper.selectPage(page, wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("page", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        
        return result;
    }

    @Override
    public void toggleRule(Long id, Boolean enabled) {
        AlertRule rule = baseMapper.selectById(id);
        if (rule == null) {
            throw new RuntimeException("告警规则不存在");
        }
        
        rule.setEnabled(enabled);
        rule.setUpdatedTime(Timestamp.from(Instant.now()));
        baseMapper.updateById(rule);
        
        log.info("告警规则 {} 状态已更新为: {}", id, enabled ? "启用" : "禁用");
    }

    @Override
    @Transactional
    public void checkRule(Long ruleId) {
        AlertRule rule = baseMapper.selectById(ruleId);
        if (rule == null || !rule.getEnabled()) {
            return;
        }
        
        checkRuleMetrics(rule);
    }

    @Override
    @Transactional
    public void checkAllRules() {
        List<AlertRule> rules = ((AlertRuleMapper) baseMapper).selectEnabledRules();
        log.info("开始检查 {} 条告警规则", rules.size());
        
        for (AlertRule rule : rules) {
            try {
                checkRuleMetrics(rule);
            } catch (Exception e) {
                log.error("检查告警规则失败: {}", rule.getRuleName(), e);
            }
        }
    }

    /**
     * 检查规则指标
     */
    private void checkRuleMetrics(AlertRule rule) {
        if (!"METRIC".equals(rule.getRuleType())) {
            log.warn("暂不支持的规则类型: {}", rule.getRuleType());
            return;
        }
        
        // 查询最近的指标数据
        Timestamp endTime = Timestamp.from(Instant.now());
        Timestamp startTime = new Timestamp(endTime.getTime() - rule.getDuration() * 1000);
        
        List<com.qoobot.qooerp.monitor.entity.MonitorMetrics> metrics = 
            metricsMapper.selectByMetricNameAndTimeRange(
                rule.getMetricName(), startTime, endTime
            );
        
        if (metrics.isEmpty()) {
            log.debug("规则 {} 无指标数据", rule.getRuleName());
            return;
        }
        
        // 按服务分组检查
        Map<String, List<com.qoobot.qooerp.monitor.entity.MonitorMetrics>> grouped = 
            metrics.stream()
                .filter(m -> rule.getServiceName() == null || rule.getServiceName().equals(m.getServiceName()))
                .collect(Collectors.groupingBy(com.qoobot.qooerp.monitor.entity.MonitorMetrics::getServiceName));
        
        for (Map.Entry<String, List<com.qoobot.qooerp.monitor.entity.MonitorMetrics>> entry : grouped.entrySet()) {
            String serviceName = entry.getKey();
            List<com.qoobot.qooerp.monitor.entity.MonitorMetrics> serviceMetrics = entry.getValue();
            
            // 检查是否满足告警条件
            for (com.qoobot.qooerp.monitor.entity.MonitorMetrics metric : serviceMetrics) {
                if (evaluateCondition(metric.getMetricValue(), rule)) {
                    handleFiringAlert(rule, metric);
                } else {
                    handleResolvingAlert(rule, metric);
                }
            }
        }
    }

    /**
     * 评估条件
     */
    private boolean evaluateCondition(Double value, AlertRule rule) {
        switch (rule.getConditionType()) {
            case "GT":
                return value > rule.getThreshold();
            case "LT":
                return value < rule.getThreshold();
            case "GTE":
                return value >= rule.getThreshold();
            case "LTE":
                return value <= rule.getThreshold();
            case "EQ":
                return value.equals(rule.getThreshold());
            case "NEQ":
                return !value.equals(rule.getThreshold());
            default:
                return false;
        }
    }

    /**
     * 处理触发告警
     */
    private void handleFiringAlert(AlertRule rule, com.qoobot.qooerp.monitor.entity.MonitorMetrics metric) {
        AlertInstance instance = alertInstanceMapper.selectByRuleAndService(
            rule.getId(), metric.getServiceName(), metric.getInstance()
        );
        
        if (instance == null) {
            // 创建新的告警实例
            instance = new AlertInstance();
            instance.setRuleId(rule.getId());
            instance.setRuleName(rule.getRuleName());
            instance.setSeverity(rule.getSeverity());
            instance.setStatus("FIRING");
            instance.setFiringTime(Timestamp.from(Instant.now()));
            instance.setServiceName(metric.getServiceName());
            instance.setInstance(metric.getInstance());
            instance.setMetricName(metric.getMetricName());
            instance.setCurrentValue(metric.getMetricValue());
            instance.setThreshold(rule.getThreshold());
            instance.setAlertTitle(rule.getAlertTitle());
            instance.setAlertMessage(formatAlertMessage(rule.getAlertMessage(), metric));
            instance.setLabels(metric.getTags());
            instance.setNotifyCount(0);
            instance.setTenantId(rule.getTenantId());
            alertInstanceMapper.insert(instance);
            
            // 记录历史
            recordHistory(instance, "FIRING", "告警触发", "system");
            
            // 发送通知
            notificationService.sendNotification(rule, instance);
            
            log.info("创建告警实例: {}", instance.getAlertTitle());
        } else if ("PENDING".equals(instance.getStatus())) {
            // 从 PENDING 转为 FIRING
            instance.setStatus("FIRING");
            alertInstanceMapper.updateById(instance);
            
            recordHistory(instance, "FIRING", "告警确认", "system");
            notificationService.sendNotification(rule, instance);
            
            log.info("告警实例转为触发状态: {}", instance.getAlertTitle());
        }
    }

    /**
     * 处理告警恢复
     */
    private void handleResolvingAlert(AlertRule rule, com.qoobot.qooerp.monitor.entity.MonitorMetrics metric) {
        AlertInstance instance = alertInstanceMapper.selectByRuleAndService(
            rule.getId(), metric.getServiceName(), metric.getInstance()
        );
        
        if (instance != null && ("PENDING".equals(instance.getStatus()) || "FIRING".equals(instance.getStatus()))) {
            instance.setStatus("RESOLVED");
            instance.setResolvedTime(Timestamp.from(Instant.now()));
            alertInstanceMapper.updateStatus(instance.getId(), "RESOLVED", instance.getResolvedTime());
            
            // 计算持续时间
            long duration = (instance.getResolvedTime().getTime() - instance.getFiringTime().getTime()) / 1000;
            instance.setDuration(duration);
            alertInstanceMapper.updateById(instance);
            
            recordHistory(instance, "RESOLVED", "告警恢复", "system");
            
            log.info("告警实例已恢复: {}", instance.getAlertTitle());
        }
    }

    /**
     * 格式化告警消息
     */
    private String formatAlertMessage(String template, com.qoobot.qooerp.monitor.entity.MonitorMetrics metric) {
        return template
            .replace("${service}", metric.getServiceName())
            .replace("${instance}", metric.getInstance())
            .replace("${metric}", metric.getMetricName())
            .replace("${value}", String.valueOf(metric.getMetricValue()))
            .replace("${unit}", metric.getMetricUnit() != null ? metric.getMetricUnit() : "");
    }

    /**
     * 记录告警历史
     */
    private void recordHistory(AlertInstance instance, String statusChange, String reason, String operator) {
        AlertHistory history = new AlertHistory();
        history.setInstanceId(instance.getId());
        history.setRuleId(instance.getRuleId());
        history.setRuleName(instance.getRuleName());
        history.setSeverity(instance.getSeverity());
        history.setStatusChange(statusChange);
        history.setChangeTime(Timestamp.from(Instant.now()));
        history.setChangeReason(reason);
        history.setOperator(operator);
        history.setAlertTitle(instance.getAlertTitle());
        history.setAlertMessage(instance.getAlertMessage());
        history.setTenantId(instance.getTenantId());
        alertHistoryMapper.insert(history);
    }

    @Override
    public Map<String, Object> listInstances(AlertInstanceQuery query) {
        LambdaQueryWrapper<AlertInstance> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getRuleId() != null) {
            wrapper.eq(AlertInstance::getRuleId, query.getRuleId());
        }
        if (query.getSeverity() != null) {
            wrapper.eq(AlertInstance::getSeverity, query.getSeverity());
        }
        if (query.getStatus() != null) {
            wrapper.eq(AlertInstance::getStatus, query.getStatus());
        }
        if (query.getServiceName() != null) {
            wrapper.like(AlertInstance::getServiceName, query.getServiceName());
        }
        if (query.getStartTime() != null) {
            wrapper.ge(AlertInstance::getFiringTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(AlertInstance::getFiringTime, query.getEndTime());
        }
        
        wrapper.orderByDesc(AlertInstance::getFiringTime);
        
        Page<AlertInstance> page = new Page<>(query.getPage(), query.getSize());
        page = alertInstanceMapper.selectPage(page, wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("page", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        
        return result;
    }

    @Override
    public AlertInstance getInstance(Long id) {
        return alertInstanceMapper.selectById(id);
    }

    @Override
    @Transactional
    public void acknowledgeInstance(Long id, String reason) {
        AlertInstance instance = alertInstanceMapper.selectById(id);
        if (instance == null) {
            throw new RuntimeException("告警实例不存在");
        }
        
        if (!"FIRING".equals(instance.getStatus())) {
            throw new RuntimeException("只能确认告警中的实例");
        }
        
        // 记录历史
        recordHistory(instance, "ACKNOWLEDGED", reason, "user");
        
        log.info("确认告警实例: {}", instance.getAlertTitle());
    }

    @Override
    @Transactional
    public void resolveInstance(Long id, String reason) {
        AlertInstance instance = alertInstanceMapper.selectById(id);
        if (instance == null) {
            throw new RuntimeException("告警实例不存在");
        }
        
        instance.setStatus("RESOLVED");
        instance.setResolvedTime(Timestamp.from(Instant.now()));
        instance.setDuration((instance.getResolvedTime().getTime() - instance.getFiringTime().getTime()) / 1000);
        alertInstanceMapper.updateById(instance);
        
        recordHistory(instance, "RESOLVED", reason, "user");
        
        log.info("解决告警实例: {}", instance.getAlertTitle());
    }

    @Override
    public List<AlertHistory> listHistory(Long instanceId) {
        LambdaQueryWrapper<AlertHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertHistory::getInstanceId, instanceId)
               .orderByDesc(AlertHistory::getChangeTime);
        return alertHistoryMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计各状态告警数量
        LambdaQueryWrapper<AlertInstance> firingWrapper = new LambdaQueryWrapper<>();
        firingWrapper.in(AlertInstance::getStatus, Arrays.asList("PENDING", "FIRING"));
        stats.put("firing", alertInstanceMapper.selectCount(firingWrapper));
        
        LambdaQueryWrapper<AlertInstance> resolvedWrapper = new LambdaQueryWrapper<>();
        resolvedWrapper.eq(AlertInstance::getStatus, "RESOLVED");
        stats.put("resolved", alertInstanceMapper.selectCount(resolvedWrapper));
        
        // 统计各严重级别
        LambdaQueryWrapper<AlertInstance> criticalWrapper = new LambdaQueryWrapper<>();
        criticalWrapper.eq(AlertInstance::getSeverity, "CRITICAL")
                      .in(AlertInstance::getStatus, Arrays.asList("PENDING", "FIRING"));
        stats.put("critical", alertInstanceMapper.selectCount(criticalWrapper));
        
        LambdaQueryWrapper<AlertInstance> warningWrapper = new LambdaQueryWrapper<>();
        warningWrapper.eq(AlertInstance::getSeverity, "WARNING")
                     .in(AlertInstance::getStatus, Arrays.asList("PENDING", "FIRING"));
        stats.put("warning", alertInstanceMapper.selectCount(warningWrapper));
        
        // 统计规则数量
        stats.put("totalRules", baseMapper.selectCount(null));
        
        LambdaQueryWrapper<AlertRule> enabledRuleWrapper = new LambdaQueryWrapper<>();
        enabledRuleWrapper.eq(AlertRule::getEnabled, true);
        stats.put("enabledRules", baseMapper.selectCount(enabledRuleWrapper));
        
        return stats;
    }

    @Override
    public void setSilence(Long id, Timestamp silenceStart, Timestamp silenceEnd) {
        AlertRule rule = baseMapper.selectById(id);
        if (rule == null) {
            throw new RuntimeException("告警规则不存在");
        }
        
        rule.setSilenceStart(silenceStart);
        rule.setSilenceEnd(silenceEnd);
        rule.setUpdatedTime(Timestamp.from(Instant.now()));
        baseMapper.updateById(rule);
        
        log.info("告警规则 {} 已设置静默", id);
    }

    @Override
    public void cancelSilence(Long id) {
        AlertRule rule = baseMapper.selectById(id);
        if (rule == null) {
            throw new RuntimeException("告警规则不存在");
        }
        
        rule.setSilenceStart(null);
        rule.setSilenceEnd(null);
        rule.setUpdatedTime(Timestamp.from(Instant.now()));
        baseMapper.updateById(rule);
        
        log.info("告警规则 {} 已取消静默", id);
    }

    @Override
    public void renotifyInstance(Long id) {
        AlertInstance instance = alertInstanceMapper.selectById(id);
        if (instance == null) {
            throw new RuntimeException("告警实例不存在");
        }
        
        AlertRule rule = baseMapper.selectById(instance.getRuleId());
        if (rule == null) {
            throw new RuntimeException("告警规则不存在");
        }
        
        notificationService.sendNotification(rule, instance);
        alertInstanceMapper.incrementNotifyCount(id);
        
        log.info("重新发送告警通知: {}", instance.getAlertTitle());
    }
}
