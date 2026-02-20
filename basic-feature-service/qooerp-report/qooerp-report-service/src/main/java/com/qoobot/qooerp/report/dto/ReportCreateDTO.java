package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 报表创建DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报表名称
     */
    private String reportName;

    /**
     * 报表类型(1-列表,2-图表,3-混合,4-交叉,5-透视)
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
