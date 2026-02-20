package com.qoobot.qooerp.scheduler.notify.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.scheduler.job.dto.ScheduleNotifyDTO;
import com.qoobot.qooerp.scheduler.notify.service.ScheduleNotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 任务报警 Controller
 */
@Tag(name = "任务报警管理", description = "任务报警相关接口")
@RestController
@RequestMapping("/notifies")
@RequiredArgsConstructor
public class ScheduleNotifyController {

    private final ScheduleNotifyService notifyService;

    @Operation(summary = "查询报警记录")
    @GetMapping
    public Result<Page<ScheduleNotifyDTO>> query(
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(notifyService.queryNotifies(jobId, status, current, size));
    }

    @Operation(summary = "处理报警")
    @PostMapping("/{notifyId}/handle")
    public Result<Void> handle(
            @PathVariable Long notifyId,
            @RequestParam String handler,
            @RequestParam(required = false) String handleRemark) {
        notifyService.handleNotify(notifyId, handler, handleRemark);
        return Result.success();
    }
}
