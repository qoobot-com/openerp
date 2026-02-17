package com.qoobot.qooerp.notify.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.notify.common.Result;
import com.qoobot.qooerp.notify.dto.NotifyRecordDTO;
import com.qoobot.qooerp.notify.dto.NotifyRecordQueryDTO;
import com.qoobot.qooerp.notify.dto.StatisticsDTO;
import com.qoobot.qooerp.notify.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 记录查询控制器
 */
@Tag(name = "记录查询", description = "记录查询相关接口")
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "记录分页查询")
    @GetMapping("/page")
    public Result<Page<NotifyRecordDTO>> page(NotifyRecordQueryDTO dto) {
        Page<NotifyRecordDTO> page = recordService.page(dto);
        return Result.success(page);
    }

    @Operation(summary = "获取记录详情")
    @GetMapping("/{id}")
    public Result<NotifyRecordDTO> get(@PathVariable Long id) {
        NotifyRecordDTO record = recordService.get(id);
        return Result.success(record);
    }

    @Operation(summary = "重试发送")
    @PostMapping("/{id}/retry")
    public Result<Void> retry(@PathVariable Long id) {
        recordService.retry(id);
        return Result.success();
    }

    @Operation(summary = "统计分析")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(@Valid StatisticsDTO dto) {
        Map<String, Object> result = recordService.statistics(dto);
        return Result.success(result);
    }
}
