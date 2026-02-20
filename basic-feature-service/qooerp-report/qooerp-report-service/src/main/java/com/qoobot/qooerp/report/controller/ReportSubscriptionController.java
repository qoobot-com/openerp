package com.qoobot.qooerp.report.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.report.dto.ReportSubscriptionCreateDTO;
import com.qoobot.qooerp.report.dto.ReportSubscriptionDTO;
import com.qoobot.qooerp.report.dto.ReportSubscriptionQueryDTO;
import com.qoobot.qooerp.report.dto.ReportSubscriptionUpdateDTO;
import com.qoobot.qooerp.report.service.ReportSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报表订阅管理", description = "报表订阅相关接口")
@RestController
@RequestMapping("/api/report/subscription")
@RequiredArgsConstructor
public class ReportSubscriptionController {

    private final ReportSubscriptionService subscriptionService;

    @Operation(summary = "创建报表订阅")
    @PostMapping
    public Result<ReportSubscriptionDTO> create(@Valid @RequestBody ReportSubscriptionCreateDTO dto) {
        return Result.success(subscriptionService.create(dto));
    }

    @Operation(summary = "更新报表订阅")
    @PutMapping("/{id}")
    public Result<ReportSubscriptionDTO> update(@PathVariable Long id, @Valid @RequestBody ReportSubscriptionUpdateDTO dto) {
        return Result.success(subscriptionService.update(id, dto));
    }

    @Operation(summary = "删除报表订阅")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        subscriptionService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取报表订阅详情")
    @GetMapping("/{id}")
    public Result<ReportSubscriptionDTO> getById(@PathVariable Long id) {
        return Result.success(subscriptionService.getById(id));
    }

    @Operation(summary = "分页查询报表订阅")
    @PostMapping("/page")
    public Result<PageResult<ReportSubscriptionDTO>> queryPage(@RequestBody ReportSubscriptionQueryDTO dto) {
        return Result.success(subscriptionService.queryPage(dto));
    }

    @Operation(summary = "暂停订阅")
    @PutMapping("/{id}/pause")
    public Result<Void> pause(@PathVariable Long id) {
        subscriptionService.pause(id);
        return Result.success();
    }

    @Operation(summary = "恢复订阅")
    @PutMapping("/{id}/resume")
    public Result<Void> resume(@PathVariable Long id) {
        subscriptionService.resume(id);
        return Result.success();
    }

    @Operation(summary = "立即执行")
    @PutMapping("/{id}/execute")
    public Result<Void> executeNow(@PathVariable Long id) {
        subscriptionService.executeNow(id);
        return Result.success();
    }
}
