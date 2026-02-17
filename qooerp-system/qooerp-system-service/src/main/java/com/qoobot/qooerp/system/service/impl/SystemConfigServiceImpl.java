package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.system.dto.SystemConfigDTO;
import com.qoobot.qooerp.system.entity.SystemConfig;
import com.qoobot.qooerp.system.mapper.SystemConfigMapper;
import com.qoobot.qooerp.system.service.SystemConfigService;
import com.qoobot.qooerp.system.vo.SystemConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统参数服务实现
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_PREFIX = "system:param:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:param", allEntries = true)
    public Long createConfig(SystemConfigDTO dto) {
        // 检查参数键是否存在
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, dto.getConfigKey());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("参数键已存在");
        }

        SystemConfig config = new SystemConfig();
        BeanUtils.copyProperties(dto, config);
        config.setIsSystem(dto.getIsSystem() == null ? 0 : dto.getIsSystem());
        baseMapper.insert(config);
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:param", allEntries = true)
    public boolean updateConfig(Long id, SystemConfigDTO dto) {
        SystemConfig config = baseMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("参数不存在");
        }

        // 系统参数不可修改键名
        if (config.getIsSystem() == 1 && dto.getConfigKey() != null 
                && !dto.getConfigKey().equals(config.getConfigKey())) {
            throw new BusinessException("系统参数不可修改键名");
        }

        // 如果修改键名，检查是否冲突
        if (dto.getConfigKey() != null && !dto.getConfigKey().equals(config.getConfigKey())) {
            LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemConfig::getConfigKey, dto.getConfigKey()).ne(SystemConfig::getId, id);
            if (baseMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("参数键已存在");
            }
        }

        BeanUtils.copyProperties(dto, config);
        return baseMapper.updateById(config) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:param", allEntries = true)
    public boolean deleteConfig(Long id) {
        SystemConfig config = baseMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("参数不存在");
        }

        // 系统参数不可删除
        if (config.getIsSystem() == 1) {
            throw new BusinessException("系统参数不可删除");
        }

        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public SystemConfigVO getConfig(Long id) {
        SystemConfig config = baseMapper.selectById(id);
        if (config == null) {
            return null;
        }
        SystemConfigVO vo = new SystemConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }

    @Override
    @Cacheable(value = "system:param", key = "#configKey")
    public String getConfigValue(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = baseMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Cacheable(value = "system:param", key = "'detail:' + #configKey")
    public SystemConfigVO getConfigByKey(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = baseMapper.selectOne(wrapper);
        if (config == null) {
            return null;
        }
        SystemConfigVO vo = new SystemConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }

    @Override
    public Page<SystemConfigVO> pageConfig(Long current, Long size, String configKey, String configName) {
        Page<SystemConfig> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        
        if (configKey != null && !configKey.isEmpty()) {
            wrapper.like(SystemConfig::getConfigKey, configKey);
        }
        if (configName != null && !configName.isEmpty()) {
            wrapper.like(SystemConfig::getConfigName, configName);
        }
        
        wrapper.orderByDesc(SystemConfig::getCreateTime);
        
        Page<SystemConfig> resultPage = baseMapper.selectPage(page, wrapper);
        
        Page<SystemConfigVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<SystemConfigVO> voList = resultPage.getRecords().stream().map(config -> {
            SystemConfigVO vo = new SystemConfigVO();
            BeanUtils.copyProperties(config, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:param", allEntries = true)
    public boolean batchUpdateConfig(List<SystemConfigDTO> dtoList) {
        for (SystemConfigDTO dto : dtoList) {
            if (dto.getId() != null) {
                updateConfig(dto.getId(), dto);
            }
        }
        return true;
    }

    @Override
    public void refreshCache() {
        redisTemplate.delete(redisTemplate.keys(CACHE_PREFIX + "*"));
        log.info("系统参数缓存已刷新");
    }
}
