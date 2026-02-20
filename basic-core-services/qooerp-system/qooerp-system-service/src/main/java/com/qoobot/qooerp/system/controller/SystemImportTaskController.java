package com.qoobot.qooerp.system.controller;

import com.qoobot.qooerp.common.response.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.system.dto.SystemImportTaskDTO;
import com.qoobot.qooerp.system.dto.SystemImportTaskQueryDTO;
import com.qoobot.qooerp.system.entity.SystemImportError;
import com.qoobot.qooerp.system.entity.SystemImportTask;
import com.qoobot.qooerp.system.service.SystemImportTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 导入任务Controller
 */
@RestController
@RequestMapping("/api/system/import")
@RequiredArgsConstructor
public class SystemImportTaskController {

    private final SystemImportTaskService importTaskService;

    /**
     * 创建导入任务
     */
    @PostMapping("/task")
    public Result<SystemImportTask> create(@RequestBody SystemImportTaskDTO dto) {
        // TODO: 从上下文获取当前用户信息
        Long creatorId = 1L;
        String creatorName = "admin";
        return Result.success(importTaskService.create(dto, creatorId, creatorName));
    }

    /**
     * 分页查询导入任务
     */
    @GetMapping("/task/page")
    public Result<IPage<SystemImportTask>> page(SystemImportTaskQueryDTO query) {
        return Result.success(importTaskService.page(query));
    }

    /**
     * 获取导入任务详情
     */
    @GetMapping("/task/{id}")
    public Result<SystemImportTask> getById(@PathVariable Long id) {
        return Result.success(importTaskService.getById(id));
    }

    /**
     * 获取导入错误详情
     */
    @GetMapping("/task/{taskId}/errors")
    public Result<List<SystemImportError>> getErrors(@PathVariable Long taskId) {
        return Result.success(importTaskService.getImportErrors(taskId));
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/template/{moduleName}")
    public void downloadTemplate(@PathVariable String moduleName, HttpServletResponse response) {
        importTaskService.downloadTemplate(moduleName, response);
    }
}
