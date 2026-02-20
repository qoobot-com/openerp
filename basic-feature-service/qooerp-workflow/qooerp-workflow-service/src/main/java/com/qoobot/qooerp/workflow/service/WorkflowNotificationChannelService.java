package com.qoobot.qooerp.workflow.service;

import java.util.List;
import java.util.Map;

/**
 * 通知方式集成服务
 * 支持多种通知渠道（邮件、短信、企业微信、钉钉等）
 */
public interface WorkflowNotificationChannelService {

    /**
     * 发送通知
     * @param channel 通知渠道（email, sms, wechat, dingtalk等）
     * @param receivers 接收者列表
     * @param title 标题
     * @param content 内容
     * @param data 附加数据
     * @return 发送结果
     */
    Map<String, Object> sendNotification(String channel, List<String> receivers,
                                      String title, String content, Map<String, Object> data);

    /**
     * 批量发送通知
     * @param notifications 通知列表
     * @return 发送结果
     */
    Map<String, Object> batchSendNotifications(List<Map<String, Object>> notifications);

    /**
     * 获取支持的通知渠道
     * @return 渠道列表
     */
    List<Map<String, Object>> getSupportedChannels();

    /**
     * 获取渠道配置
     * @param channel 渠道名称
     * @return 配置信息
     */
    Map<String, Object> getChannelConfig(String channel);

    /**
     * 设置渠道配置
     * @param channel 渠道名称
     * @param config 配置信息
     */
    void setChannelConfig(String channel, Map<String, Object> config);

    /**
     * 测试通知发送
     * @param channel 渠道名称
     * @param receiver 接收者
     * @return 测试结果
     */
    Map<String, Object> testChannel(String channel, String receiver);

    /**
     * 获取通知发送历史
     * @param channel 渠道名称
     * @param limit 限制数量
     * @return 发送历史
     */
    List<Map<String, Object>> getNotificationHistory(String channel, int limit);

    /**
     * 获取通知发送统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计信息
     */
    Map<String, Object> getNotificationStatistics(String startDate, String endDate);
}
