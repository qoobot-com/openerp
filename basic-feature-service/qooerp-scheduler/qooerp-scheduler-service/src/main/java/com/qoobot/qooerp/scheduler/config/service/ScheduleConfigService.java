package com.qoobot.qooerp.scheduler.config.service;

import java.util.Map;

/**
 * 任务配置 Service 接口
 */
public interface ScheduleConfigService {

    /**
     * 设置任务配置
     */
    void setConfig(Long jobId, String configKey, String configValue, String configDesc);

    /**
     * 获取任务配置
     */
    Map<String, String> getConfig(Long jobId);

    /**
     * 删除任务配置
     */
    void deleteConfig(Long jobId, String configKey);
}
