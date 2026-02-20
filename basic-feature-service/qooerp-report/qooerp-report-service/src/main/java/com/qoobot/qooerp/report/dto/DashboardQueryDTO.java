package com.qoobot.qooerp.report.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class DashboardQueryDTO implements Serializable {
    private String dashboardName;
    private Integer dashboardType;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
