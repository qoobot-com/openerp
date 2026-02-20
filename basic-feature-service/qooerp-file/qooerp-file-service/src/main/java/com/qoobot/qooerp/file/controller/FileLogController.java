package com.qoobot.qooerp.file.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.file.dto.FileLogDTO;
import com.qoobot.qooerp.file.service.FileLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文件操作日志控制器
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Tag(name = "文件日志", description = "文件操作日志查询接口")
@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class FileLogController {

    private final FileLogService logService;

    @Operation(summary = "日志列表")
    @GetMapping("/list")
    public Result<PageResult<FileLogDTO>> listLogs(@RequestParam(value = "fileId", required = false) Long fileId,
                                                      @RequestParam(value = "operation", required = false) String operation,
                                                      @RequestParam(defaultValue = "1") Integer pageNum,
                                                      @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(logService.listLogs(fileId, operation, pageNum, pageSize));
    }
}
