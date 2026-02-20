package com.qoobot.qooerp.workflow.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.service.WorkflowNotificationChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知方式集成服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowNotificationChannelServiceImpl implements WorkflowNotificationChannelService {

    // 渠道配置缓存
    private final Map<String, Map<String, Object>> channelConfigCache = new ConcurrentHashMap<>();

    // 通知发送历史
    private final Map<String, List<Map<String, Object>>> notificationHistoryCache = new ConcurrentHashMap<>();

    // 支持的通知渠道
    private final List<Map<String, Object>> supportedChannels;

    public WorkflowNotificationChannelServiceImpl() {
        this.supportedChannels = initSupportedChannels();
        initDefaultChannelConfigs();
    }

    @Override
    public Map<String, Object> sendNotification(String channel, List<String> receivers,
                                              String title, String content, Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 验证渠道是否支持
            if (!isChannelSupported(channel)) {
                result.put("success", false);
                result.put("message", "不支持的通知渠道: " + channel);
                return result;
            }

            // 获取渠道配置
            Map<String, Object> config = channelConfigCache.get(channel);
            if (config == null || !Boolean.TRUE.equals(config.get("enabled"))) {
                result.put("success", false);
                result.put("message", "通知渠道未启用或未配置: " + channel);
                return result;
            }

            // 根据渠道类型发送通知
            int successCount = 0;
            int failedCount = 0;
            List<String> errors = new ArrayList<>();

            for (String receiver : receivers) {
                try {
                    boolean sent = sendByChannel(channel, receiver, title, content, data, config);
                    if (sent) {
                        successCount++;
                    } else {
                        failedCount++;
                        errors.add(receiver + ": 发送失败");
                    }

                    // 记录发送历史
                    recordNotificationHistory(channel, receiver, title, sent, null);

                } catch (Exception e) {
                    failedCount++;
                    errors.add(receiver + ": " + e.getMessage());
                    recordNotificationHistory(channel, receiver, title, false, e.getMessage());
                    log.error("发送通知失败: channel={}, receiver={}", channel, receiver, e);
                }
            }

            result.put("success", failedCount == 0);
            result.put("total", receivers.size());
            result.put("successCount", successCount);
            result.put("failedCount", failedCount);
            result.put("errors", errors);

            log.info("发送通知完成: channel={}, total={}, success={}, failed={}",
                    channel, receivers.size(), successCount, failedCount);

        } catch (Exception e) {
            log.error("批量发送通知失败: channel={}", channel, e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> batchSendNotifications(List<Map<String, Object>> notifications) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", notifications.size());
        result.put("successCount", 0);
        result.put("failedCount", 0);
        result.put("details", new ArrayList<>());

        for (Map<String, Object> notification : notifications) {
            String channel = (String) notification.get("channel");
            @SuppressWarnings("unchecked")
            List<String> receivers = (List<String>) notification.get("receivers");
            String title = (String) notification.get("title");
            String content = (String) notification.get("content");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) notification.getOrDefault("data", new HashMap<>());

            Map<String, Object> sendResult = sendNotification(channel, receivers, title, content, data);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> details = (List<Map<String, Object>>) result.get("details");
            details.add(sendResult);

            if (Boolean.TRUE.equals(sendResult.get("success"))) {
                result.put("successCount", (Integer) result.get("successCount") + 1);
            } else {
                result.put("failedCount", (Integer) result.get("failedCount") + 1);
            }
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getSupportedChannels() {
        return new ArrayList<>(supportedChannels);
    }

    @Override
    public Map<String, Object> getChannelConfig(String channel) {
        Map<String, Object> config = channelConfigCache.get(channel);
        if (config != null) {
            // 返回配置副本，避免修改缓存
            return new HashMap<>(config);
        }
        return new HashMap<>();
    }

    @Override
    public void setChannelConfig(String channel, Map<String, Object> config) {
        channelConfigCache.put(channel, config);
        log.info("设置通知渠道配置: channel={}", channel);
    }

    @Override
    public Map<String, Object> testChannel(String channel, String receiver) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> config = channelConfigCache.get(channel);
            if (config == null || !Boolean.TRUE.equals(config.get("enabled"))) {
                result.put("success", false);
                result.put("message", "通知渠道未启用或未配置");
                return result;
            }

            // 发送测试消息
            boolean sent = sendByChannel(channel, receiver, "QooERP工作流测试通知",
                    "这是一条测试通知，如果您收到此消息，说明通知渠道配置正确。", null, config);

            result.put("success", sent);
            result.put("message", sent ? "测试成功" : "测试失败");

            if (sent) {
                recordNotificationHistory(channel, receiver, "测试通知", true, null);
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            log.error("测试通知渠道失败: channel={}, receiver={}", channel, receiver, e);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getNotificationHistory(String channel, int limit) {
        List<Map<String, Object>> history = notificationHistoryCache.get(channel);
        if (history == null) {
            return new ArrayList<>();
        }

        // 返回最近的N条记录
        if (limit > 0 && history.size() > limit) {
            return new ArrayList<>(history.subList(0, limit));
        }

        return new ArrayList<>(history);
    }

    @Override
    public Map<String, Object> getNotificationStatistics(String startDate, String endDate) {
        Map<String, Object> statistics = new HashMap<>();

        try {
            int totalSent = 0;
            int totalSuccess = 0;
            int totalFailed = 0;
            Map<String, Integer> channelStats = new HashMap<>();

            for (Map.Entry<String, List<Map<String, Object>>> entry : notificationHistoryCache.entrySet()) {
                String channel = entry.getKey();
                List<Map<String, Object>> history = entry.getValue();

                int channelSent = 0;
                int channelSuccess = 0;

                for (Map<String, Object> record : history) {
                    channelSent++;
                    totalSent++;
                    if (Boolean.TRUE.equals(record.get("success"))) {
                        channelSuccess++;
                        totalSuccess++;
                    } else {
                        totalFailed++;
                    }
                }

                channelStats.put(channel, channelSent);
                channelStats.put(channel + "_success", channelSuccess);
            }

            statistics.put("totalSent", totalSent);
            statistics.put("totalSuccess", totalSuccess);
            statistics.put("totalFailed", totalFailed);
            statistics.put("successRate", totalSent > 0 ? (totalSuccess * 100.0 / totalSent) : 0.0);
            statistics.put("channelStats", channelStats);

            log.info("获取通知统计: totalSent={}, totalSuccess={}, totalFailed={}, successRate={}",
                    totalSent, totalSuccess, totalFailed, statistics.get("successRate"));

        } catch (Exception e) {
            log.error("获取通知统计失败", e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getMessage());
        }

        return statistics;
    }

    /**
     * 根据渠道发送通知
     */
    private boolean sendByChannel(String channel, String receiver, String title,
                                 String content, Map<String, Object> data, Map<String, Object> config) {
        switch (channel) {
            case "email":
                return sendEmail(receiver, title, content, data, config);
            case "sms":
                return sendSms(receiver, title, content, data, config);
            case "wechat":
                return sendWeChat(receiver, title, content, data, config);
            case "dingtalk":
                return sendDingTalk(receiver, title, content, data, config);
            default:
                log.warn("不支持的通知渠道: {}", channel);
                return false;
        }
    }

    /**
     * 发送邮件通知
     */
    private boolean sendEmail(String receiver, String title, String content,
                            Map<String, Object> data, Map<String, Object> config) {
        try {
            // TODO: 实现邮件发送逻辑
            // 可以使用JavaMail API或Spring Boot的MailSender
            log.info("发送邮件通知: receiver={}, title={}", receiver, title);
            return true;
        } catch (Exception e) {
            log.error("发送邮件通知失败: receiver={}", receiver, e);
            return false;
        }
    }

    /**
     * 发送短信通知
     */
    private boolean sendSms(String receiver, String title, String content,
                          Map<String, Object> data, Map<String, Object> config) {
        try {
            // TODO: 实现短信发送逻辑
            // 可以集成阿里云、腾讯云等短信服务
            log.info("发送短信通知: receiver={}, content={}", receiver, content);
            return true;
        } catch (Exception e) {
            log.error("发送短信通知失败: receiver={}", receiver, e);
            return false;
        }
    }

    /**
     * 发送企业微信通知
     */
    private boolean sendWeChat(String receiver, String title, String content,
                              Map<String, Object> data, Map<String, Object> config) {
        try {
            // TODO: 实现企业微信通知逻辑
            // 使用企业微信API发送应用消息
            log.info("发送企业微信通知: receiver={}, title={}", receiver, title);
            return true;
        } catch (Exception e) {
            log.error("发送企业微信通知失败: receiver={}", receiver, e);
            return false;
        }
    }

    /**
     * 发送钉钉通知
     */
    private boolean sendDingTalk(String receiver, String title, String content,
                                Map<String, Object> data, Map<String, Object> config) {
        try {
            // TODO: 实现钉钉通知逻辑
            // 使用钉钉API发送工作通知
            log.info("发送钉钉通知: receiver={}, title={}", receiver, title);
            return true;
        } catch (Exception e) {
            log.error("发送钉钉通知失败: receiver={}", receiver, e);
            return false;
        }
    }

    /**
     * 检查渠道是否支持
     */
    private boolean isChannelSupported(String channel) {
        return supportedChannels.stream()
                .anyMatch(c -> channel.equals(c.get("name")));
    }

    /**
     * 记录通知发送历史
     */
    private void recordNotificationHistory(String channel, String receiver, String title,
                                          boolean success, String errorMessage) {
        Map<String, Object> record = new HashMap<>();
        record.put("channel", channel);
        record.put("receiver", receiver);
        record.put("title", title);
        record.put("success", success);
        record.put("errorMessage", errorMessage);
        record.put("sendTime", LocalDateTime.now());

        notificationHistoryCache.computeIfAbsent(channel, k -> new ArrayList<>())
                .add(0, record);  // 添加到列表开头

        // 限制历史记录数量（每个渠道最多保留1000条）
        List<Map<String, Object>> history = notificationHistoryCache.get(channel);
        if (history.size() > 1000) {
            history.subList(1000, history.size()).clear();
        }
    }

    /**
     * 初始化支持的通知渠道
     */
    private List<Map<String, Object>> initSupportedChannels() {
        List<Map<String, Object>> channels = new ArrayList<>();

        Map<String, Object> email = new HashMap<>();
        email.put("name", "email");
        email.put("displayName", "邮件通知");
        email.put("description", "通过邮件发送流程通知");
        email.put("enabled", false);
        channels.add(email);

        Map<String, Object> sms = new HashMap<>();
        sms.put("name", "sms");
        sms.put("displayName", "短信通知");
        sms.put("description", "通过短信发送流程通知");
        sms.put("enabled", false);
        channels.add(sms);

        Map<String, Object> wechat = new HashMap<>();
        wechat.put("name", "wechat");
        wechat.put("displayName", "企业微信");
        wechat.put("description", "通过企业微信应用消息发送通知");
        wechat.put("enabled", false);
        channels.add(wechat);

        Map<String, Object> dingtalk = new HashMap<>();
        dingtalk.put("name", "dingtalk");
        dingtalk.put("displayName", "钉钉");
        dingtalk.put("description", "通过钉钉工作通知发送消息");
        dingtalk.put("enabled", false);
        channels.add(dingtalk);

        return channels;
    }

    /**
     * 初始化默认渠道配置
     */
    private void initDefaultChannelConfigs() {
        // 邮件配置
        Map<String, Object> emailConfig = new HashMap<>();
        emailConfig.put("enabled", false);
        emailConfig.put("host", "");
        emailConfig.put("port", 465);
        emailConfig.put("username", "");
        emailConfig.put("password", "");
        emailConfig.put("from", "");
        emailConfig.put("ssl", true);
        channelConfigCache.put("email", emailConfig);

        // 短信配置
        Map<String, Object> smsConfig = new HashMap<>();
        smsConfig.put("enabled", false);
        smsConfig.put("provider", "");
        smsConfig.put("accessKeyId", "");
        smsConfig.put("accessKeySecret", "");
        smsConfig.put("signName", "");
        smsConfig.put("templateCode", "");
        channelConfigCache.put("sms", smsConfig);

        // 企业微信配置
        Map<String, Object> wechatConfig = new HashMap<>();
        wechatConfig.put("enabled", false);
        wechatConfig.put("corpId", "");
        wechatConfig.put("agentId", "");
        wechatConfig.put("agentSecret", "");
        channelConfigCache.put("wechat", wechatConfig);

        // 钉钉配置
        Map<String, Object> dingtalkConfig = new HashMap<>();
        dingtalkConfig.put("enabled", false);
        dingtalkConfig.put("appKey", "");
        dingtalkConfig.put("appSecret", "");
        channelConfigCache.put("dingtalk", dingtalkConfig);
    }
}
