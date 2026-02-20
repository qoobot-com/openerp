package com.qoobot.qooerp.report.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class DashboardCreateDTO implements Serializable {
    private String dashboardName;
    private Integer dashboardType;
    private String layoutConfig;
    private String description;
}
