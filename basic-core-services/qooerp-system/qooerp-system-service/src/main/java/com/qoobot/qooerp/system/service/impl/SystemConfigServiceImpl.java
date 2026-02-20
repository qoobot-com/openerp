package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.system.config.SystemProperties;
import com.qoobot.qooerp.system.dto.SystemConfigDTO;
import com.qoobot.qooerp.system.dto.SystemConfigQueryDTO;
import com.qoobot.qooerp.system.entity.SystemConfig;
import com.qoobot.qooerp.system.mapper.SystemConfigMapper;
import com.qoobot.qooerp.system.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 系统参数服务实现（带缓存）
 */
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    private final SystemConfigMapper configMapper;
    private final SystemProperties systemProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CONFIG_CACHE_PREFIX = "system:config:";
    private static final String CONFIG_VALUE_PREFIX = "system:config_value:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "config", allEntries = true)
    public Long createConfig(SystemConfigDTO configDTO) {
        // 检查参数键是否已存在
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configDTO.getConfigKey());
        if (configMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("参数键已存在");
        }

        SystemConfig config = new SystemConfig();
        BeanUtils.copyProperties(configDTO, config);
        configMapper.insert(config);

        // 清除值缓存
        clearValueCache(configDTO.getConfigKey());

        return config.getId();
    }

    @Override
    @Cacheable(value = "config", key = "#id")
    public SystemConfigDTO getConfig(Long id) {
        SystemConfig config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("参数不存在");
        }

        SystemConfigDTO dto = new SystemConfigDTO();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "config", key = "#id")
    public void updateConfig(Long id, SystemConfigDTO configDTO) {
        SystemConfig existConfig = configMapper.selectById(id);
        if (existConfig == null) {
            throw new BusinessException("参数不存在");
        }

        // 如果修改了键，检查新键是否已存在
        if (StringUtils.hasText(configDTO.getConfigKey())
            && !configDTO.getConfigKey().equals(existConfig.getConfigKey())) {
            LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemConfig::getConfigKey, configDTO.getConfigKey());
            wrapper.ne(SystemConfig::getId, id);
            if (configMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("参数键已存在");
            }

            // 清除旧键的值缓存
            clearValueCache(existConfig.getConfigKey());
        }

        SystemConfig config = new SystemConfig();
        BeanUtils.copyProperties(configDTO, config);
        config.setId(id);
        configMapper.updateById(config);

        // 清除新键的值缓存
        if (configDTO.getConfigKey() != null) {
            clearValueCache(configDTO.getConfigKey());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "config", key = "#id")
    public void deleteConfig(Long id) {
        SystemConfig config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("参数不存在");
        }

        // 系统参数不允许删除
        if (config.getIsSystem() != null && config.getIsSystem() == 1) {
            throw new BusinessException("系统参数不允许删除");
        }

        configMapper.deleteById(id);

        // 清除值缓存
        clearValueCache(config.getConfigKey());
    }

    @Override
    public IPage<SystemConfigDTO> pageConfig(Integer current, Integer size, SystemConfigQueryDTO queryDTO) {
        Page<SystemConfig> page = new Page<>(current, size);
        IPage<SystemConfig> configPage = configMapper.selectPageByQuery(page, queryDTO);

        Page<SystemConfigDTO> result = new Page<>(current, size, configPage.getTotal());
        List<SystemConfigDTO> dtoList = configPage.getRecords().stream().map(config -> {
            SystemConfigDTO dto = new SystemConfigDTO();
            BeanUtils.copyProperties(config, dto);
            return dto;
        }).toList();
        result.setRecords(dtoList);

        return result;
    }

    @Override
    public String getConfigValue(String configKey) {
        if (!systemProperties.getConfig().isCacheEnabled()) {
            return getConfigValueFromDB(configKey);
        }

        String cacheKey = CONFIG_VALUE_PREFIX + configKey;
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            return cachedValue.toString();
        }

        String value = getConfigValueFromDB(configKey);

        // 缓存1小时
        if (value != null) {
            redisTemplate.opsForValue().set(cacheKey, value, 1, TimeUnit.HOURS);
        }

        return value;
    }

    @Override
    @Cacheable(value = "config", key = "'key:' + #configKey")
    public SystemConfigDTO getConfigByKey(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = configMapper.selectOne(wrapper);
        if (config == null) {
            return null;
        }

        SystemConfigDTO dto = new SystemConfigDTO();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "config", allEntries = true)
    public void batchUpdateConfig(Map<String, String> configMap) {
        if (configMap == null || configMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String configKey = entry.getKey();
            String configValue = entry.getValue();

            LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemConfig::getConfigKey, configKey);
            SystemConfig config = configMapper.selectOne(wrapper);

            if (config != null) {
                config.setConfigValue(configValue);
                configMapper.updateById(config);

                // 清除值缓存
                clearValueCache(configKey);
            }
        }
    }

    /**
     * 从数据库获取配置值
     */
    private String getConfigValueFromDB(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        wrapper.select(SystemConfig::getConfigValue);
        SystemConfig config = configMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 清除值缓存
     */
    private void clearValueCache(String configKey) {
        if (configKey != null && !configKey.isEmpty()) {
            String cacheKey = CONFIG_VALUE_PREFIX + configKey;
            redisTemplate.delete(cacheKey);
        }
    }
}
