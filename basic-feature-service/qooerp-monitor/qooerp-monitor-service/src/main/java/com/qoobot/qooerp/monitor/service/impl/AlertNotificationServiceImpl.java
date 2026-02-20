package com.qoobot.qooerp.monitor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qoobot.qooerp.monitor.entity.AlertInstance;
import com.qoobot.qooerp.monitor.entity.AlertRule;
import com.qoobot.qooerp.monitor.service.AlertNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 告警通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertNotificationServiceImpl implements AlertNotificationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${alert.notification.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${alert.notification.sms.enabled:true}")
    private boolean smsEnabled;

    @Value("${alert.notification.webhook.enabled:true}")
    private boolean webhookEnabled;

    @Value("${alert.notification.dingtalk.enabled:true}")
    private boolean dingtalkEnabled;

    @Override
    public void sendNotification(AlertRule rule, AlertInstance instance) {
        try {
            // 解析通知渠道
            String channelsJson = rule.getNotifyChannels();
            if (channelsJson == null) {
                log.warn("告警规则未配置通知渠道: {}", rule.getRuleName());
                return;
            }

            String[] channels = objectMapper.readValue(channelsJson, String[].class);

            // 解析通知配置
            Map<String, Object> notifyConfig = new HashMap<>();
            if (rule.getNotifyConfig() != null) {
                notifyConfig = objectMapper.readValue(rule.getNotifyConfig(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            }

            // 根据渠道发送通知
            for (String channel : channels) {
                switch (channel.toUpperCase()) {
                    case "EMAIL":
                        sendEmail(rule, instance, notifyConfig);
                        break;
                    case "SMS":
                        sendSms(rule, instance, notifyConfig);
                        break;
                    case "WEBHOOK":
                        sendWebhook(rule, instance, notifyConfig);
                        break;
                    case "DINGTALK":
                        sendDingTalk(rule, instance, notifyConfig);
                        break;
                    default:
                        log.warn("不支持的通知渠道: {}", channel);
                }
            }
        } catch (Exception e) {
            log.error("发送告警通知失败: {}", instance.getAlertTitle(), e);
        }
    }

    private void sendEmail(AlertRule rule, AlertInstance instance, Map<String, Object> config) {
        if (!emailEnabled) {
            return;
        }

        try {
            // TODO: 集成邮件服务（如 JavaMail 或 SendGrid）
            // 这里先记录日志
            log.info("发送邮件通知: 标题={}, 内容={}", instance.getAlertTitle(), instance.getAlertMessage());
        } catch (Exception e) {
            log.error("发送邮件通知失败", e);
        }
    }

    private void sendSms(AlertRule rule, AlertInstance instance, Map<String, Object> config) {
        if (!smsEnabled) {
            return;
        }

        try {
            // TODO: 集成短信服务（如阿里云短信、腾讯云短信）
            log.info("发送短信通知: {}", instance.getAlertMessage());
        } catch (Exception e) {
            log.error("发送短信通知失败", e);
        }
    }

    private void sendWebhook(AlertRule rule, AlertInstance instance, Map<String, Object> config) {
        if (!webhookEnabled) {
            return;
        }

        try {
            String url = (String) config.get("webhook_url");
            if (url == null || url.isEmpty()) {
                log.warn("未配置 Webhook URL");
                return;
            }

            // 构造通知载荷
            Map<String, Object> payload = new HashMap<>();
            payload.put("alert_id", instance.getId());
            payload.put("rule_id", rule.getId());
            payload.put("rule_name", rule.getRuleName());
            payload.put("severity", instance.getSeverity());
            payload.put("status", instance.getStatus());
            payload.put("title", instance.getAlertTitle());
            payload.put("message", instance.getAlertMessage());
            payload.put("service_name", instance.getServiceName());
            payload.put("instance", instance.getInstance());
            payload.put("metric_name", instance.getMetricName());
            payload.put("current_value", instance.getCurrentValue());
            payload.put("threshold", instance.getThreshold());
            payload.put("firing_time", instance.getFiringTime());
            payload.put("timestamp", System.currentTimeMillis());

            sendWebhook(url, payload);
        } catch (Exception e) {
            log.error("发送 Webhook 通知失败", e);
        }
    }

    private void sendDingTalk(AlertRule rule, AlertInstance instance, Map<String, Object> config) {
        if (!dingtalkEnabled) {
            return;
        }

        try {
            String webhook = (String) config.get("dingtalk_webhook");
            if (webhook == null || webhook.isEmpty()) {
                log.warn("未配置钉钉 Webhook URL");
                return;
            }

            // 构造钉钉消息
            StringBuilder message = new StringBuilder();
            message.append("## 告警通知\n\n");
            message.append("**告警级别**: ").append(instance.getSeverity()).append("\n\n");
            message.append("**告警标题**: ").append(instance.getAlertTitle()).append("\n\n");
            message.append("**告警内容**: ").append(instance.getAlertMessage()).append("\n\n");
            message.append("**服务名称**: ").append(instance.getServiceName()).append("\n\n");
            message.append("**实例标识**: ").append(instance.getInstance()).append("\n\n");
            message.append("**当前值**: ").append(instance.getCurrentValue()).append("\n\n");
            message.append("**阈值**: ").append(instance.getThreshold()).append("\n\n");
            message.append("**触发时间**: ").append(instance.getFiringTime()).append("\n\n");

            // 发送钉钉通知
            Map<String, Object> payload = new HashMap<>();
            payload.put("msgtype", "markdown");
            
            Map<String, Object> markdown = new HashMap<>();
            markdown.put("title", instance.getAlertTitle());
            markdown.put("text", message.toString());
            payload.put("markdown", markdown);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);
            ResponseEntity<String> response = restTemplate.exchange(webhook, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("钉钉通知发送成功");
            } else {
                log.error("钉钉通知发送失败: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("发送钉钉通知失败", e);
        }
    }

    @Override
    public void sendEmail(String[] to, String subject, String content) {
        log.info("发送邮件通知: to={}, subject={}", Arrays.toString(to), subject);
        // TODO: 实现邮件发送
    }

    @Override
    public void sendSms(String[] phones, String message) {
        log.info("发送短信通知: phones={}, message={}", Arrays.toString(phones), message);
        // TODO: 实现短信发送
    }

    @Override
    public void sendWebhook(String url, Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Webhook 通知发送成功: {}", url);
            } else {
                log.error("Webhook 通知发送失败: {}, status: {}", url, response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("发送 Webhook 通知失败: {}", url, e);
        }
    }

    @Override
    public void sendDingTalk(String webhook, String message) {
        log.info("发送钉钉通知: {}", message);
        // TODO: 实现钉钉通知
    }
}
