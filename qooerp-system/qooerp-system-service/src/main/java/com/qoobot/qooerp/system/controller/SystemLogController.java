package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.dto.SystemLogDTO;
import com.qoobot.qooerp.system.service.SystemLogService;
import com.qoobot.qooerp.system.vo.SystemLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志Controller
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Tag(name = "日志管理", description = "日志管理接口")
@RestController
@RequestMapping("/api/system/log")
@RequiredArgsConstructor
public class SystemLogController {

    private final SystemLogService logService;

    @Operation(summary = "保存日志")
    @PostMapping
    public Result<Long> save(@Valid @RequestBody SystemLogDTO dto) {
        Long id = logService.saveLog(dto);
        return Result.success(id);
    }

    @Operation(summary = "获取日志详情")
    @GetMapping("/{id}")
    public Result<SystemLogVO> getById(@Parameter(description = "日志ID") @PathVariable Long id) {
        SystemLogVO vo = logService.getLog(id);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询日志")
    @GetMapping("/list")
    public Result<Page<SystemLogVO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "日志类型") @RequestParam(required = false) String logType,
            @Parameter(description = "模块名称") @RequestParam(required = false) String module,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "IP地址") @RequestParam(required = false) String ip,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) {
        Page<SystemLogVO> page = logService.pageLog(current, size, logType, module, username, ip, status, startTime, endTime);
        return Result.success(page);
    }

    @Operation(summary = "日志统计")
    @GetMapping("/statistics")
    public Result<Object> statistics(
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        Object result = logService.statistics(startDate, endDate);
        return Result.success(result);
    }

    @Operation(summary = "批量删除日志")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        logService.batchDeleteLog(ids);
        return Result.success();
    }

    @Operation(summary = "删除日志")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "日志ID") @PathVariable Long id) {
        logService.batchDeleteLog(List.of(id));
        return Result.success();
    }

    @Operation(summary = "清理过期日志")
    @PostMapping("/clean")
    public Result<Integer> cleanExpireLogs(@Parameter(description = "保留天数") @RequestParam(defaultValue = "90") Integer days) {
        Integer count = logService.cleanExpireLogs(days);
        return Result.success(count);
    }
}
