package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 仪表盘DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class DashboardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String dashboardNo;
    private String dashboardName;
    private Integer dashboardType;
    private String layoutConfig;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private Long createBy;
}
