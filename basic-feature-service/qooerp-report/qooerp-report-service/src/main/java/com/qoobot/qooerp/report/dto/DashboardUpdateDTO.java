package com.qoobot.qooerp.report.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class DashboardUpdateDTO implements Serializable {
    private Long id;
    private String dashboardName;
    private String layoutConfig;
    private String description;
}
