package com.qoobot.qooerp.report.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.report.dto.DashboardCreateDTO;
import com.qoobot.qooerp.report.dto.DashboardDTO;
import com.qoobot.qooerp.report.dto.DashboardQueryDTO;
import com.qoobot.qooerp.report.dto.DashboardUpdateDTO;
import com.qoobot.qooerp.report.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/dashboards")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PostMapping
    public Result<DashboardDTO> createDashboard(@Validated @RequestBody DashboardCreateDTO dto) {
        return Result.success(dashboardService.createDashboard(dto));
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateDashboard(@PathVariable Long id, @Validated @RequestBody DashboardUpdateDTO dto) {
        dto.setId(id);
        return Result.success(dashboardService.updateDashboard(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteDashboard(@PathVariable Long id) {
        return Result.success(dashboardService.deleteDashboard(id));
    }

    @GetMapping("/{id}")
    public Result<DashboardDTO> getDashboardById(@PathVariable Long id) {
        return Result.success(dashboardService.getDashboardById(id));
    }

    @GetMapping
    public Result<PageResult<DashboardDTO>> queryDashboards(DashboardQueryDTO queryDTO) {
        return Result.success(dashboardService.queryDashboards(queryDTO));
    }

    @PostMapping("/{id}/publish")
    public Result<Boolean> publishDashboard(@PathVariable Long id) {
        return Result.success(dashboardService.publishDashboard(id));
    }
}
