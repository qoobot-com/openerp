package com.qoobot.qooerp.notify.service;

import com.qoobot.qooerp.notify.dto.ChannelConfigDTO;

import java.util.Map;

/**
 * 配置服务接口
 */
public interface ConfigService {

    /**
     * 获取配置
     */
    Object getConfig(String key);

    /**
     * 更新配置
     */
    void updateConfig(String key, String value);

    /**
     * 获取所有配置
     */
    Map<String, Object> getAllConfigs();

    /**
     * 更新渠道配置
     */
    void updateChannelConfig(ChannelConfigDTO dto);
}
