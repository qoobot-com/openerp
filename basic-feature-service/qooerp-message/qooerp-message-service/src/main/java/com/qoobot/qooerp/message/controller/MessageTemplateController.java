package com.qoobot.qooerp.message.controller;

import com.qoobot.qooerp.common.response.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.*;
import com.qoobot.qooerp.message.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message/template")
@RequiredArgsConstructor
public class MessageTemplateController {

    private final MessageTemplateService templateService;

    @PostMapping("/create")
    public Result<Long> createTemplate(@RequestBody MessageTemplateCreateDTO dto) {
        Long id = templateService.createTemplate(dto);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> updateTemplate(@PathVariable Long id, @RequestBody MessageTemplateCreateDTO dto) {
        templateService.updateTemplate(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<MessageTemplateDTO> getTemplateById(@PathVariable Long id) {
        MessageTemplateDTO dto = templateService.getTemplateById(id);
        return Result.success(dto);
    }

    @PostMapping("/list")
    public Result<Page<MessageTemplateDTO>> queryTemplates(@RequestBody MessageTemplateQueryDTO dto) {
        Page<MessageTemplateDTO> page = templateService.queryTemplates(dto);
        return Result.success(page);
    }

    @PostMapping("/preview")
    public Result<MessageDTO> previewTemplate(@RequestBody TemplatePreviewDTO dto) {
        MessageDTO messageDTO = templateService.previewTemplate(dto);
        return Result.success(messageDTO);
    }
}
