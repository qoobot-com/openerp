package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 报表查询DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报表名称(模糊查询)
     */
    private String reportName;

    /**
     * 报表类型
     */
    private Integer reportType;

    /**
     * 报表分类
     */
    private String category;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
