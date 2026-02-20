package com.qoobot.qooerp.report.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.report.dto.ReportHistoryDTO;
import com.qoobot.qooerp.report.dto.ReportHistoryQueryDTO;
import com.qoobot.qooerp.report.service.ReportHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报表历史管理", description = "报表历史相关接口")
@RestController
@RequestMapping("/api/report/history")
@RequiredArgsConstructor
public class ReportHistoryController {

    private final ReportHistoryService historyService;

    @Operation(summary = "创建报表快照")
    @PostMapping("/snapshot/{reportId}")
    public Result<ReportHistoryDTO> createSnapshot(@PathVariable Long reportId, @RequestBody String snapshotData) {
        return Result.success(historyService.createSnapshot(reportId, snapshotData, null));
    }

    @Operation(summary = "获取报表历史详情")
    @GetMapping("/{id}")
    public Result<ReportHistoryDTO> getById(@PathVariable Long id) {
        return Result.success(historyService.getById(id));
    }

    @Operation(summary = "分页查询报表历史")
    @PostMapping("/page")
    public Result<PageResult<ReportHistoryDTO>> queryPage(@RequestBody ReportHistoryQueryDTO dto) {
        return Result.success(historyService.queryPage(dto));
    }

    @Operation(summary = "删除报表历史")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        historyService.delete(id);
        return Result.success();
    }

    @Operation(summary = "删除报表所有历史")
    @DeleteMapping("/report/{reportId}")
    public Result<Void> deleteByReportId(@PathVariable Long reportId) {
        historyService.deleteByReportId(reportId);
        return Result.success();
    }
}
