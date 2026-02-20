package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemConfigDTO;
import com.qoobot.qooerp.system.dto.SystemConfigQueryDTO;
import com.qoobot.qooerp.system.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统参数服务接口
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 创建参数
     */
    Long createConfig(SystemConfigDTO configDTO);

    /**
     * 获取参数
     */
    SystemConfigDTO getConfig(Long id);

    /**
     * 更新参数
     */
    void updateConfig(Long id, SystemConfigDTO configDTO);

    /**
     * 删除参数
     */
    void deleteConfig(Long id);

    /**
     * 分页查询参数
     */
    IPage<SystemConfigDTO> pageConfig(Integer current, Integer size, SystemConfigQueryDTO queryDTO);

    /**
     * 根据键获取参数值
     */
    String getConfigValue(String configKey);

    /**
     * 根据键获取参数
     */
    SystemConfigDTO getConfigByKey(String configKey);

    /**
     * 批量更新参数
     */
    void batchUpdateConfig(Map<String, String> configMap);
}
