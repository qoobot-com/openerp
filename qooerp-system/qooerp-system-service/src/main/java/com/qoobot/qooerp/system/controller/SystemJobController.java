package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.dto.SystemJobDTO;
import com.qoobot.qooerp.system.service.SystemJobService;
import com.qoobot.qooerp.system.vo.SystemJobVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务Controller
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Tag(name = "定时任务管理", description = "定时任务管理接口")
@RestController
@RequestMapping("/api/system/job")
@RequiredArgsConstructor
public class SystemJobController {

    private final SystemJobService jobService;

    @Operation(summary = "创建定时任务")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SystemJobDTO dto) {
        Long id = jobService.createJob(dto);
        return Result.success(id);
    }

    @Operation(summary = "获取任务详情")
    @GetMapping("/{id}")
    public Result<SystemJobVO> getById(@Parameter(description = "任务ID") @PathVariable Long id) {
        SystemJobVO vo = jobService.getJob(id);
        return Result.success(vo);
    }

    @Operation(summary = "更新定时任务")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "任务ID") @PathVariable Long id,
                                @Valid @RequestBody SystemJobDTO dto) {
        jobService.updateJob(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除定时任务")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "任务ID") @PathVariable Long id) {
        jobService.deleteJob(id);
        return Result.success();
    }

    @Operation(summary = "分页查询定时任务")
    @GetMapping("/list")
    public Result<Page<SystemJobVO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "任务名称") @RequestParam(required = false) String jobName,
            @Parameter(description = "任务组") @RequestParam(required = false) String jobGroup,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Page<SystemJobVO> page = jobService.pageJob(current, size, jobName, jobGroup, status);
        return Result.success(page);
    }

    @Operation(summary = "启用任务")
    @PostMapping("/{id}/start")
    public Result<Void> start(@Parameter(description = "任务ID") @PathVariable Long id) {
        jobService.startJob(id);
        return Result.success();
    }

    @Operation(summary = "停用任务")
    @PostMapping("/{id}/stop")
    public Result<Void> stop(@Parameter(description = "任务ID") @PathVariable Long id) {
        jobService.stopJob(id);
        return Result.success();
    }

    @Operation(summary = "立即执行任务")
    @PostMapping("/{id}/execute")
    public Result<Object> execute(@Parameter(description = "任务ID") @PathVariable Long id) {
        Object result = jobService.executeJob(id);
        return Result.success(result);
    }
}
