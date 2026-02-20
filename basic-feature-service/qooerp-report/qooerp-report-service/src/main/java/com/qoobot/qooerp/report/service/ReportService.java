package com.qoobot.qooerp.report.service;

import com.qoobot.qooerp.report.dto.ReportCreateDTO;
import com.qoobot.qooerp.report.dto.ReportDTO;
import com.qoobot.qooerp.report.dto.ReportQueryDTO;
import com.qoobot.qooerp.report.dto.ReportUpdateDTO;
import com.qoobot.qooerp.common.response.PageResult;

/**
 * 报表服务接口
 *
 * @author Auto
 * @since 2026-02-18
 */
public interface ReportService {

    /**
     * 创建报表
     *
     * @param dto 报表创建DTO
     * @return 报表DTO
     */
    ReportDTO createReport(ReportCreateDTO dto);

    /**
     * 更新报表
     *
     * @param dto 报表更新DTO
     * @return 是否成功
     */
    boolean updateReport(ReportUpdateDTO dto);

    /**
     * 删除报表
     *
     * @param id 报表ID
     * @return 是否成功
     */
    boolean deleteReport(Long id);

    /**
     * 获取报表详情
     *
     * @param id 报表ID
     * @return 报表DTO
     */
    ReportDTO getReportById(Long id);

    /**
     * 分页查询报表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<ReportDTO> queryReports(ReportQueryDTO queryDTO);

    /**
     * 发布报表
     *
     * @param id 报表ID
     * @return 是否成功
     */
    boolean publishReport(Long id);

    /**
     * 归档报表
     *
     * @param id 报表ID
     * @return 是否成功
     */
    boolean archiveReport(Long id);
}
