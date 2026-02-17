package com.qoobot.qooerp.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.notify.dto.ChannelConfigDTO;
import com.qoobot.qooerp.notify.entity.NotifyConfig;
import com.qoobot.qooerp.notify.mapper.NotifyConfigMapper;
import com.qoobot.qooerp.notify.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置服务实现
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final NotifyConfigMapper configMapper;
    private static final String CONFIG_CACHE_PREFIX = "notify:config:";

    @Override
    public Object getConfig(String key) {
        // 先从缓存获取
        Object value = redisTemplate.opsForValue().get(CONFIG_CACHE_PREFIX + key);
        if (value != null) {
            return value;
        }

        // 缓存未命中，从数据库获取
        LambdaQueryWrapper<NotifyConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyConfig::getConfigKey, key);
        NotifyConfig config = configMapper.selectOne(wrapper);

        if (config != null) {
            // 存入缓存
            redisTemplate.opsForValue().set(CONFIG_CACHE_PREFIX + key, config.getConfigValue());
            return config.getConfigValue();
        }

        return null;
    }

    @Override
    public void updateConfig(String key, String value) {
        // 更新数据库
        LambdaQueryWrapper<NotifyConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyConfig::getConfigKey, key);
        NotifyConfig config = configMapper.selectOne(wrapper);

        if (config != null) {
            config.setConfigValue(value);
            configMapper.updateById(config);
        } else {
            config = new NotifyConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            configMapper.insert(config);
        }

        // 更新缓存
        redisTemplate.opsForValue().set(CONFIG_CACHE_PREFIX + key, value);
    }

    @Override
    public Map<String, Object> getAllConfigs() {
        Map<String, Object> configs = new HashMap<>();

        // 从数据库加载所有配置
        List<NotifyConfig> configList = configMapper.selectList(new LambdaQueryWrapper<>());
        for (NotifyConfig config : configList) {
            configs.put(config.getConfigKey(), config.getConfigValue());
        }

        return configs;
    }

    @Override
    public void updateChannelConfig(ChannelConfigDTO dto) {
        String channelType = dto.getChannelType();
        Map<String, Object> config = dto.getConfig();

        // 将配置项存入Redis和数据库
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String key = channelType + "." + entry.getKey();
            updateConfig(key, entry.getValue().toString());
        }
    }
}
