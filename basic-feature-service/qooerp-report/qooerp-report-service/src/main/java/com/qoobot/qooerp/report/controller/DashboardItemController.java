package com.qoobot.qooerp.report.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.report.dto.DashboardItemCreateDTO;
import com.qoobot.qooerp.report.dto.DashboardItemDTO;
import com.qoobot.qooerp.report.dto.DashboardItemQueryDTO;
import com.qoobot.qooerp.report.dto.DashboardItemUpdateDTO;
import com.qoobot.qooerp.report.service.DashboardItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "仪表盘项管理", description = "仪表盘项相关接口")
@RestController
@RequestMapping("/api/report/dashboard-item")
@RequiredArgsConstructor
public class DashboardItemController {

    private final DashboardItemService dashboardItemService;

    @Operation(summary = "创建仪表盘项")
    @PostMapping
    public Result<DashboardItemDTO> create(@Valid @RequestBody DashboardItemCreateDTO dto) {
        return Result.success(dashboardItemService.create(dto));
    }

    @Operation(summary = "更新仪表盘项")
    @PutMapping("/{id}")
    public Result<DashboardItemDTO> update(@PathVariable Long id, @Valid @RequestBody DashboardItemUpdateDTO dto) {
        return Result.success(dashboardItemService.update(id, dto));
    }

    @Operation(summary = "删除仪表盘项")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dashboardItemService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取仪表盘项详情")
    @GetMapping("/{id}")
    public Result<DashboardItemDTO> getById(@PathVariable Long id) {
        return Result.success(dashboardItemService.getById(id));
    }

    @Operation(summary = "分页查询仪表盘项")
    @PostMapping("/page")
    public Result<PageResult<DashboardItemDTO>> queryPage(@RequestBody DashboardItemQueryDTO dto) {
        return Result.success(dashboardItemService.queryPage(dto));
    }

    @Operation(summary = "根据仪表盘ID查询项列表")
    @GetMapping("/dashboard/{dashboardId}")
    public Result<List<DashboardItemDTO>> getByDashboardId(@PathVariable Long dashboardId) {
        return Result.success(dashboardItemService.getByDashboardId(dashboardId));
    }

    @Operation(summary = "更新排序")
    @PutMapping("/{id}/sort")
    public Result<Void> updateSort(@PathVariable Long id, @RequestParam Integer sortOrder) {
        dashboardItemService.updateSort(id, sortOrder);
        return Result.success();
    }
}
