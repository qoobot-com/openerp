package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 报表更新DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 报表名称
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
     * 报表描述
     */
    private String description;
}
