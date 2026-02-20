package com.qoobot.qooerp.report.service;

import com.qoobot.qooerp.report.dto.ReportConfigCreateDTO;
import com.qoobot.qooerp.report.dto.ReportConfigDTO;
import com.qoobot.qooerp.report.dto.ReportConfigQueryDTO;
import com.qoobot.qooerp.report.dto.ReportConfigUpdateDTO;
import com.qoobot.qooerp.common.response.PageResult;

/**
 * 报表配置服务接口
 *
 * @author Auto
 * @since 2026-02-18
 */
public interface ReportConfigService {

    /**
     * 创建配置
     *
     * @param dto 配置创建DTO
     * @return 配置DTO
     */
    ReportConfigDTO createConfig(ReportConfigCreateDTO dto);

    /**
     * 更新配置
     *
     * @param dto 配置更新DTO
     * @return 是否成功
     */
    boolean updateConfig(ReportConfigUpdateDTO dto);

    /**
     * 删除配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    boolean deleteConfig(Long id);

    /**
     * 获取配置详情
     *
     * @param id 配置ID
     * @return 配置DTO
     */
    ReportConfigDTO getConfigById(Long id);

    /**
     * 分页查询配置
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<ReportConfigDTO> queryConfigs(ReportConfigQueryDTO queryDTO);
}
