package com.qoobot.qooerp.scheduler.job.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.scheduler.job.dto.JobExecuteDTO;
import com.qoobot.qooerp.scheduler.job.dto.JobQueryDTO;
import com.qoobot.qooerp.scheduler.job.dto.ScheduleJobDTO;
import com.qoobot.qooerp.scheduler.job.service.ScheduleJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务 Controller
 */
@Tag(name = "定时任务管理", description = "定时任务相关接口")
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class ScheduleJobController {

    private final ScheduleJobService jobService;

    @Operation(summary = "创建任务")
    @PostMapping
    public Result<ScheduleJobDTO> create(@RequestBody ScheduleJobDTO dto) {
        return Result.success(jobService.createJob(dto));
    }

    @Operation(summary = "更新任务")
    @PutMapping("/{id}")
    public Result<ScheduleJobDTO> update(@PathVariable Long id, @RequestBody ScheduleJobDTO dto) {
        return Result.success(jobService.updateJob(id, dto));
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        jobService.deleteJob(id);
        return Result.success();
    }

    @Operation(summary = "查询任务")
    @GetMapping("/{id}")
    public Result<ScheduleJobDTO> getById(@PathVariable Long id) {
        return Result.success(jobService.getJobById(id));
    }

    @Operation(summary = "分页查询任务")
    @GetMapping
    public Result<Page<ScheduleJobDTO>> query(JobQueryDTO queryDTO) {
        return Result.success(jobService.queryJobs(queryDTO));
    }

    @Operation(summary = "启动任务")
    @PostMapping("/{id}/start")
    public Result<Void> start(@PathVariable Long id) {
        jobService.startJob(id);
        return Result.success();
    }

    @Operation(summary = "停止任务")
    @PostMapping("/{id}/stop")
    public Result<Void> stop(@PathVariable Long id) {
        jobService.stopJob(id);
        return Result.success();
    }

    @Operation(summary = "暂停任务")
    @PostMapping("/{id}/pause")
    public Result<Void> pause(@PathVariable Long id) {
        jobService.pauseJob(id);
        return Result.success();
    }

    @Operation(summary = "恢复任务")
    @PostMapping("/{id}/resume")
    public Result<Void> resume(@PathVariable Long id) {
        jobService.resumeJob(id);
        return Result.success();
    }

    @Operation(summary = "手动执行任务")
    @PostMapping("/execute")
    public Result<Void> execute(@RequestBody JobExecuteDTO dto) {
        jobService.executeJob(dto);
        return Result.success();
    }

    @Operation(summary = "获取任务日志")
    @GetMapping("/{id}/logs")
    public Result<Page<com.qoobot.qooerp.scheduler.job.dto.ScheduleLogDTO>> getLogs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(jobService.getJobLogs(id, current, size));
    }
}
