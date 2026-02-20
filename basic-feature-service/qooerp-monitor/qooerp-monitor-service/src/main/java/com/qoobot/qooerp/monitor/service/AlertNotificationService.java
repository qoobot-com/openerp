package com.qoobot.qooerp.monitor.service;

import com.qoobot.qooerp.monitor.entity.AlertInstance;
import com.qoobot.qooerp.monitor.entity.AlertRule;

import java.util.Map;

/**
 * 告警通知服务接口
 */
public interface AlertNotificationService {

    /**
     * 发送告警通知
     *
     * @param rule     告警规则
     * @param instance 告警实例
     */
    void sendNotification(AlertRule rule, AlertInstance instance);

    /**
     * 发送邮件通知
     *
     * @param to       收件人列表
     * @param subject  主题
     * @param content  内容
     */
    void sendEmail(String[] to, String subject, String content);

    /**
     * 发送短信通知
     *
     * @param phones   手机号列表
     * @param message  消息内容
     */
    void sendSms(String[] phones, String message);

    /**
     * 发送 Webhook 通知
     *
     * @param url      Webhook URL
     * @param payload  通知载荷
     */
    void sendWebhook(String url, Map<String, Object> payload);

    /**
     * 发送钉钉通知
     *
     * @param webhook  钉钉 Webhook URL
     * @param message  消息内容
     */
    void sendDingTalk(String webhook, String message);
}
