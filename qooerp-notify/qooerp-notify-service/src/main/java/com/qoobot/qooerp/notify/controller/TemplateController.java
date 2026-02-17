package com.qoobot.qooerp.notify.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.notify.common.Result;
import com.qoobot.qooerp.notify.dto.*;
import com.qoobot.qooerp.notify.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 模板控制器
 */
@Tag(name = "模板管理", description = "模板管理相关接口")
@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @Operation(summary = "创建模板")
    @PostMapping("/create")
    public Result<Long> create(@Valid @RequestBody TemplateCreateDTO dto) {
        Long id = templateService.create(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新模板")
    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody TemplateUpdateDTO dto) {
        templateService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取模板详情")
    @GetMapping("/{id}")
    public Result<TemplateDTO> get(@PathVariable Long id) {
        TemplateDTO template = templateService.get(id);
        return Result.success(template);
    }

    @Operation(summary = "模板分页查询")
    @GetMapping("/page")
    public Result<Page<TemplateDTO>> page(TemplateQueryDTO dto) {
        Page<TemplateDTO> page = templateService.page(dto);
        return Result.success(page);
    }

    @Operation(summary = "模板预览")
    @PostMapping("/preview")
    public Result<Map<String, Object>> preview(@Valid @RequestBody TemplatePreviewDTO dto) {
        Map<String, Object> result = templateService.preview(dto);
        return Result.success(result);
    }

    @Operation(summary = "查询记录分页")
    @GetMapping("/records/page")
    public Result<Page<NotifyRecordDTO>> pageRecords(NotifyRecordQueryDTO dto) {
        Page<NotifyRecordDTO> page = templateService.pageRecords(dto);
        return Result.success(page);
    }

    @Operation(summary = "获取记录详情")
    @GetMapping("/records/{id}")
    public Result<NotifyRecordDTO> getRecord(@PathVariable Long id) {
        NotifyRecordDTO record = templateService.getRecord(id);
        return Result.success(record);
    }
}
