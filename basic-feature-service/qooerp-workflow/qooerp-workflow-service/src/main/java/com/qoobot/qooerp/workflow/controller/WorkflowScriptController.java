package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.service.WorkflowScriptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程脚本控制器
 */
@Tag(name = "流程脚本管理", description = "流程脚本的执行、验证、管理等功能")
@RestController
@RequestMapping("/script")
@RequiredArgsConstructor
public class WorkflowScriptController {

    private final WorkflowScriptService workflowScriptService;

    @Operation(summary = "执行脚本")
    @PostMapping("/execute")
    public Result<Object> executeScript(@RequestBody Map<String, Object> request) {
        String scriptType = (String) request.get("scriptType");
        String scriptContent = (String) request.get("scriptContent");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");

        Object result = workflowScriptService.executeScript(scriptType, scriptContent, variables);
        return Result.success(result);
    }

    @Operation(summary = "验证脚本语法")
    @PostMapping("/validate")
    public Result<Map<String, Object>> validateScript(@RequestBody Map<String, Object> request) {
        String scriptType = (String) request.get("scriptType");
        String scriptContent = (String) request.get("scriptContent");

        Map<String, Object> result = workflowScriptService.validateScript(scriptType, scriptContent);
        return Result.success(result);
    }

    @Operation(summary = "格式化脚本")
    @PostMapping("/format")
    public Result<String> formatScript(@RequestBody Map<String, Object> request) {
        String scriptType = (String) request.get("scriptType");
        String scriptContent = (String) request.get("scriptContent");

        String formatted = workflowScriptService.formatScript(scriptType, scriptContent);
        return Result.success(formatted);
    }

    @Operation(summary = "获取脚本模板")
    @GetMapping("/template/{templateName}")
    public Result<String> getScriptTemplate(@PathVariable String templateName) {
        String template = workflowScriptService.getScriptTemplate(templateName);
        return Result.success(template);
    }

    @Operation(summary = "获取所有脚本模板")
    @GetMapping("/templates")
    public Result<Map<String, String>> getScriptTemplates() {
        // TODO: 从服务中获取所有模板
        Map<String, String> templates = Map.of(
                "javascript_condition", "JavaScript条件判断模板",
                "javascript_expression", "JavaScript表达式模板",
                "groovy_condition", "Groovy条件判断模板",
                "groovy_expression", "Groovy表达式模板"
        );
        return Result.success(templates);
    }

    @Operation(summary = "测试脚本执行")
    @PostMapping("/test")
    public Result<Map<String, Object>> testScript(@RequestBody Map<String, Object> request) {
        String scriptType = (String) request.get("scriptType");
        String scriptContent = (String) request.get("scriptContent");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");

        Map<String, Object> result = workflowScriptService.testScript(scriptType, scriptContent, variables);
        return Result.success(result);
    }
}
