package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 报表编号
     */
    private String reportNo;

    /**
     * 报表名称
     */
    private String reportName;

    /**
     * 报表类型
     */
    private Integer reportType;

    /**
     * 报表类型描述
     */
    private String reportTypeDesc;

    /**
     * 报表分类
     */
    private String category;

    /**
     * 报表描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createBy;
}
