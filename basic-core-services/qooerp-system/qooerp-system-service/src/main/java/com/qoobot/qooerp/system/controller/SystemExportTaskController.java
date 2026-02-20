package com.qoobot.qooerp.system.controller;

import com.qoobot.qooerp.common.response.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.system.dto.SystemExportTaskDTO;
import com.qoobot.qooerp.system.dto.SystemExportTaskQueryDTO;
import com.qoobot.qooerp.system.entity.SystemExportTask;
import com.qoobot.qooerp.system.service.SystemExportTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 导出任务Controller
 */
@RestController
@RequestMapping("/api/system/export")
@RequiredArgsConstructor
public class SystemExportTaskController {

    private final SystemExportTaskService exportTaskService;

    /**
     * 创建导出任务
     */
    @PostMapping("/task")
    public Result<SystemExportTask> create(@RequestBody SystemExportTaskDTO dto) {
        // TODO: 从上下文获取当前用户信息
        Long creatorId = 1L;
        String creatorName = "admin";
        return Result.success(exportTaskService.create(dto, creatorId, creatorName));
    }

    /**
     * 分页查询导出任务
     */
    @GetMapping("/task/page")
    public Result<IPage<SystemExportTask>> page(SystemExportTaskQueryDTO query) {
        return Result.success(exportTaskService.page(query));
    }

    /**
     * 获取导出任务详情
     */
    @GetMapping("/task/{id}")
    public Result<SystemExportTask> getById(@PathVariable Long id) {
        return Result.success(exportTaskService.getById(id));
    }

    /**
     * 下载导出文件
     */
    @GetMapping("/task/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response) {
        exportTaskService.downloadFile(id, response);
    }

    /**
     * 删除导出任务
     */
    @DeleteMapping("/task/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        exportTaskService.delete(id);
        return Result.success();
    }
}
