package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.dto.SystemLogDTO;
import com.qoobot.qooerp.system.dto.SystemLogQueryDTO;
import com.qoobot.qooerp.system.entity.SystemLog;
import com.qoobot.qooerp.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/system/log")
@RequiredArgsConstructor
public class SystemLogController {

    private final SystemLogService logService;

    /**
     * 获取日志
     */
    @GetMapping("/{id}")
    public Result<SystemLogDTO> getLog(@PathVariable Long id) {
        SystemLogDTO logDTO = logService.getLog(id);
        return Result.success(logDTO);
    }

    /**
     * 分页查询日志
     */
    @GetMapping("/list")
    public Result<IPage<SystemLogDTO>> pageLog(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            SystemLogQueryDTO queryDTO) {
        IPage<SystemLogDTO> page = logService.pageLog(current, size, queryDTO);
        return Result.success(page);
    }

    /**
     * 日志统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(SystemLogQueryDTO queryDTO) {
        Map<String, Object> statistics = logService.getStatistics(queryDTO);
        return Result.success(statistics);
    }

    /**
     * 删除日志
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteLog(@PathVariable Long id) {
        logService.deleteLog(id);
        return Result.success();
    }

    /**
     * 批量删除日志
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteLog(@RequestParam String ids) {
        logService.batchDeleteLog(ids);
        return Result.success();
    }

    /**
     * 记录日志
     */
    @PostMapping
    public Result<Void> saveLog(@RequestBody SystemLog log) {
        logService.saveLog(log);
        return Result.success();
    }
}
