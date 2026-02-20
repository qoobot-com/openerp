package com.qoobot.qooerp.scheduler.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleConfig;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleConfigMapper;
import com.qoobot.qooerp.scheduler.config.service.ScheduleConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务配置 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleConfigServiceImpl implements ScheduleConfigService {

    private final ScheduleConfigMapper configMapper;

    @Override
    public void setConfig(Long jobId, String configKey, String configValue, String configDesc) {
        log.info("设置任务配置, jobId: {}, configKey: {}", jobId, configKey);

        LambdaQueryWrapper<ScheduleConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleConfig::getJobId, jobId);
        wrapper.eq(ScheduleConfig::getConfigKey, configKey);
        ScheduleConfig existingConfig = configMapper.selectOne(wrapper);

        if (existingConfig != null) {
            existingConfig.setConfigValue(configValue);
            existingConfig.setConfigDesc(configDesc);
            configMapper.updateById(existingConfig);
        } else {
            ScheduleConfig config = new ScheduleConfig();
            config.setJobId(jobId);
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setConfigDesc(configDesc);
            config.setTenantId(1L); // TODO: 从上下文获取租户ID
            configMapper.insert(config);
        }
    }

    @Override
    public Map<String, String> getConfig(Long jobId) {
        log.info("获取任务配置, jobId: {}", jobId);

        LambdaQueryWrapper<ScheduleConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleConfig::getJobId, jobId);
        List<ScheduleConfig> configs = configMapper.selectList(wrapper);

        return configs.stream()
                .collect(Collectors.toMap(ScheduleConfig::getConfigKey, ScheduleConfig::getConfigValue));
    }

    @Override
    public void deleteConfig(Long jobId, String configKey) {
        log.info("删除任务配置, jobId: {}, configKey: {}", jobId, configKey);

        LambdaQueryWrapper<ScheduleConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleConfig::getJobId, jobId);
        wrapper.eq(ScheduleConfig::getConfigKey, configKey);
        configMapper.delete(wrapper);
    }
}
