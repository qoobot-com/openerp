package com.qoobot.qooerp.scheduler.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleConfig;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 配置缓存服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigCacheServiceImpl {

    private final ScheduleConfigMapper scheduleConfigMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CONFIG_CACHE_PREFIX = "schedule:config:";
    private static final long CACHE_EXPIRE_HOURS = 1;

    /**
     * 获取任务配置(带缓存)
     */
    public Map<String, String> getConfigWithCache(Long jobId) {
        String cacheKey = CONFIG_CACHE_PREFIX + jobId;

        // 先从缓存获取
        Map<Object, Object> cached = redisTemplate.opsForHash().entries(cacheKey);
        if (!cached.isEmpty()) {
            log.info("从缓存获取配置: jobId={}", jobId);
            return convertToStringMap(cached);
        }

        // 缓存未命中,从数据库加载
        Map<String, String> config = loadConfigFromDB(jobId);

        // 写入缓存
        if (!config.isEmpty()) {
            saveConfigToCache(jobId, config);
        }

        return config;
    }

    /**
     * 保存任务配置(同步到缓存)
     */
    public void saveConfigWithCache(Long jobId, String configKey, String configValue, String configDesc) {
        // 保存到数据库
        ScheduleConfig config = new ScheduleConfig();
        config.setJobId(jobId);
        config.setConfigKey(configKey);
        config.setConfigValue(configValue);
        config.setConfigDesc(configDesc);
        scheduleConfigMapper.insert(config);

        // 更新缓存
        String cacheKey = CONFIG_CACHE_PREFIX + jobId;
        redisTemplate.opsForHash().put(cacheKey, configKey, configValue);
        redisTemplate.expire(cacheKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        log.info("配置已保存并更新缓存: jobId={}, configKey={}", jobId, configKey);
    }

    /**
     * 更新任务配置(同步到缓存)
     */
    public void updateConfigWithCache(Long jobId, String configKey, String configValue, String configDesc) {
        // 更新数据库
        LambdaQueryWrapper<ScheduleConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleConfig::getJobId, jobId).eq(ScheduleConfig::getConfigKey, configKey);
        ScheduleConfig config = scheduleConfigMapper.selectOne(wrapper);
        
        if (config != null) {
            config.setConfigValue(configValue);
            config.setConfigDesc(configDesc);
            scheduleConfigMapper.updateById(config);

            // 更新缓存
            String cacheKey = CONFIG_CACHE_PREFIX + jobId;
            redisTemplate.opsForHash().put(cacheKey, configKey, configValue);
            redisTemplate.expire(cacheKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            
            log.info("配置已更新并同步缓存: jobId={}, configKey={}", jobId, configKey);
        }
    }

    /**
     * 删除任务配置(从缓存移除)
     */
    public void deleteConfigWithCache(Long jobId, String configKey) {
        // 删除数据库记录
        LambdaQueryWrapper<ScheduleConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleConfig::getJobId, jobId).eq(ScheduleConfig::getConfigKey, configKey);
        scheduleConfigMapper.delete(wrapper);

        // 从缓存移除
        String cacheKey = CONFIG_CACHE_PREFIX + jobId;
        redisTemplate.opsForHash().delete(cacheKey, configKey);
        
        log.info("配置已删除并从缓存移除: jobId={}, configKey={}", jobId, configKey);
    }

    /**
     * 删除任务的所有缓存
     */
    public void deleteAllConfigCache(Long jobId) {
        String cacheKey = CONFIG_CACHE_PREFIX + jobId;
        redisTemplate.delete(cacheKey);
        log.info("任务所有配置缓存已删除: jobId={}", jobId);
    }

    /**
     * 刷新任务配置缓存
     */
    public void refreshConfigCache(Long jobId) {
        deleteAllConfigCache(jobId);
        Map<String, String> config = loadConfigFromDB(jobId);
        saveConfigToCache(jobId, config);
        log.info("任务配置缓存已刷新: jobId={}", jobId);
    }

    /**
     * 从数据库加载配置
     */
    private Map<String, String> loadConfigFromDB(Long jobId) {
        LambdaQueryWrapper<ScheduleConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleConfig::getJobId, jobId);
        List<ScheduleConfig> configs = scheduleConfigMapper.selectList(wrapper);

        Map<String, String> configMap = new java.util.HashMap<>();
        for (ScheduleConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }

        return configMap;
    }

    /**
     * 保存配置到缓存
     */
    private void saveConfigToCache(Long jobId, Map<String, String> config) {
        String cacheKey = CONFIG_CACHE_PREFIX + jobId;
        redisTemplate.opsForHash().putAll(cacheKey, config);
        redisTemplate.expire(cacheKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    /**
     * 转换为String Map
     */
    private Map<String, String> convertToStringMap(Map<Object, Object> map) {
        Map<String, String> result = new java.util.HashMap<>();
        map.forEach((k, v) -> result.put(k != null ? k.toString() : "", v != null ? v.toString() : ""));
        return result;
    }
}
