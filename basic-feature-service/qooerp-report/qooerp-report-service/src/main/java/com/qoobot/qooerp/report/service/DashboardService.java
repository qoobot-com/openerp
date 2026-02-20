package com.qoobot.qooerp.report.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.report.dto.DashboardCreateDTO;
import com.qoobot.qooerp.report.dto.DashboardDTO;
import com.qoobot.qooerp.report.dto.DashboardQueryDTO;
import com.qoobot.qooerp.report.dto.DashboardUpdateDTO;

public interface DashboardService {
    DashboardDTO createDashboard(DashboardCreateDTO dto);
    boolean updateDashboard(DashboardUpdateDTO dto);
    boolean deleteDashboard(Long id);
    DashboardDTO getDashboardById(Long id);
    PageResult<DashboardDTO> queryDashboards(DashboardQueryDTO queryDTO);
    boolean publishDashboard(Long id);
}
