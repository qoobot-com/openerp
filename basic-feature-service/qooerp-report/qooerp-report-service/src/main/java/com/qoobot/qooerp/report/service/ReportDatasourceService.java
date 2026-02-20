package com.qoobot.qooerp.report.service;

import com.qoobot.qooerp.report.dto.ReportDatasourceCreateDTO;
import com.qoobot.qooerp.report.dto.ReportDatasourceDTO;
import com.qoobot.qooerp.report.dto.ReportDatasourceQueryDTO;
import com.qoobot.qooerp.report.dto.ReportDatasourceUpdateDTO;
import com.qoobot.qooerp.common.response.PageResult;

/**
 * 报表数据源服务接口
 *
 * @author Auto
 * @since 2026-02-18
 */
public interface ReportDatasourceService {

    /**
     * 创建数据源
     *
     * @param dto 数据源创建DTO
     * @return 数据源DTO
     */
    ReportDatasourceDTO createDatasource(ReportDatasourceCreateDTO dto);

    /**
     * 更新数据源
     *
     * @param dto 数据源更新DTO
     * @return 是否成功
     */
    boolean updateDatasource(ReportDatasourceUpdateDTO dto);

    /**
     * 删除数据源
     *
     * @param id 数据源ID
     * @return 是否成功
     */
    boolean deleteDatasource(Long id);

    /**
     * 获取数据源详情
     *
     * @param id 数据源ID
     * @return 数据源DTO
     */
    ReportDatasourceDTO getDatasourceById(Long id);

    /**
     * 分页查询数据源
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<ReportDatasourceDTO> queryDatasources(ReportDatasourceQueryDTO queryDTO);

    /**
     * 测试数据源连接
     *
     * @param id 数据源ID
     * @return 是否连接成功
     */
    boolean testConnection(Long id);
}
