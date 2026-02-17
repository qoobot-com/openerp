package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemConfigDTO;
import com.qoobot.qooerp.system.entity.SystemConfig;
import com.qoobot.qooerp.system.vo.SystemConfigVO;

import java.util.List;

/**
 * 系统参数服务接口
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 创建参数
     *
     * @param dto 参数DTO
     * @return 参数ID
     */
    Long createConfig(SystemConfigDTO dto);

    /**
     * 更新参数
     *
     * @param id  参数ID
     * @param dto 参数DTO
     * @return 是否成功
     */
    boolean updateConfig(Long id, SystemConfigDTO dto);

    /**
     * 删除参数
     *
     * @param id 参数ID
     * @return 是否成功
     */
    boolean deleteConfig(Long id);

    /**
     * 获取参数详情
     *
     * @param id 参数ID
     * @return 参数VO
     */
    SystemConfigVO getConfig(Long id);

    /**
     * 根据键获取参数值（缓存）
     *
     * @param configKey 参数键
     * @return 参数值
     */
    String getConfigValue(String configKey);

    /**
     * 根据键获取参数详情（缓存）
     *
     * @param configKey 参数键
     * @return 参数VO
     */
    SystemConfigVO getConfigByKey(String configKey);

    /**
     * 分页查询参数
     *
     * @param current    当前页
     * @param size       每页大小
     * @param configKey  参数键
     * @param configName 参数名称
     * @return 分页结果
     */
    Page<SystemConfigVO> pageConfig(Long current, Long size, String configKey, String configName);

    /**
     * 批量更新参数
     *
     * @param dtoList 参数DTO列表
     * @return 是否成功
     */
    boolean batchUpdateConfig(List<SystemConfigDTO> dtoList);

    /**
     * 刷新参数缓存
     */
    void refreshCache();
}
