package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.dto.ProcessDefinitionDTO;
import com.qoobot.qooerp.workflow.service.WorkflowDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 流程定义控制器
 */
@Tag(name = "流程定义管理", description = "流程定义的增删改查和部署操作")
@RestController
@RequestMapping("/definition")
@RequiredArgsConstructor
public class WorkflowDefinitionController {

    private final WorkflowDefinitionService workflowDefinitionService;

    @Operation(summary = "部署流程定义")
    @PostMapping("/deploy")
    public Result<String> deploy(
            @RequestParam("bpmnXml") String bpmnXml,
            @RequestParam("processName") String processName,
            @RequestParam("processKey") String processKey,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "formId", required = false) String formId) {
        String processDefinitionId = workflowDefinitionService.deployProcess(
                bpmnXml, processName, processKey, description, categoryId, formId);
        return Result.success(processDefinitionId);
    }

    @Operation(summary = "发布流程定义")
    @PostMapping("/publish/{processDefinitionId}")
    public Result<Void> publish(@PathVariable String processDefinitionId) {
        workflowDefinitionService.publishProcess(processDefinitionId);
        return Result.success();
    }

    @Operation(summary = "停用流程定义")
    @PostMapping("/suspend/{processDefinitionId}")
    public Result<Void> suspend(@PathVariable String processDefinitionId) {
        workflowDefinitionService.suspendProcess(processDefinitionId);
        return Result.success();
    }

    @Operation(summary = "激活流程定义")
    @PostMapping("/activate/{processDefinitionId}")
    public Result<Void> activate(@PathVariable String processDefinitionId) {
        workflowDefinitionService.activateProcess(processDefinitionId);
        return Result.success();
    }

    @Operation(summary = "删除流程定义")
    @DeleteMapping("/delete/{deploymentId}")
    public Result<Void> delete(
            @PathVariable String deploymentId,
            @Parameter(description = "是否级联删除") @RequestParam(defaultValue = "false") boolean cascade) {
        workflowDefinitionService.deleteProcess(deploymentId, cascade);
        return Result.success();
    }

    @Operation(summary = "复制流程定义")
    @PostMapping("/copy/{processDefinitionId}")
    public Result<String> copy(
            @PathVariable String processDefinitionId,
            @RequestParam("newName") String newName,
            @RequestParam("newKey") String newKey) {
        String newProcessDefinitionId = workflowDefinitionService.copyProcess(
                processDefinitionId, newName, newKey);
        return Result.success(newProcessDefinitionId);
    }

    @Operation(summary = "导出流程定义")
    @GetMapping("/export/{processDefinitionId}")
    public Result<String> export(@PathVariable String processDefinitionId) {
        String bpmnXml = workflowDefinitionService.exportProcess(processDefinitionId);
        return Result.success(bpmnXml);
    }

    @Operation(summary = "导入流程定义")
    @PostMapping("/import")
    public Result<String> importProcess(@RequestBody Map<String, String> request) {
        String bpmnXml = request.get("bpmnXml");
        String processDefinitionId = workflowDefinitionService.importProcess(bpmnXml);
        return Result.success(processDefinitionId);
    }

    @Operation(summary = "查询流程定义详情")
    @GetMapping("/detail/{processDefinitionId}")
    public Result<ProcessDefinitionDTO> getDetail(@PathVariable String processDefinitionId) {
        ProcessDefinitionDTO dto = workflowDefinitionService.getProcessDefinition(processDefinitionId);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询流程定义列表")
    @GetMapping("/page")
    public Result<PageResult<ProcessDefinitionDTO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "流程Key") @RequestParam(required = false) String processKey,
            @Parameter(description = "流程名称") @RequestParam(required = false) String processName,
            @Parameter(description = "分类ID") @RequestParam(required = false) String categoryId,
            @Parameter(description = "是否挂起") @RequestParam(required = false) Integer suspended) {
        PageResult<ProcessDefinitionDTO> result = workflowDefinitionService.pageProcessDefinitions(
                current, size, processKey, processName, categoryId, suspended);
        return Result.success(result);
    }

    @Operation(summary = "查询所有流程定义")
    @GetMapping("/list")
    public Result<List<ProcessDefinitionDTO>> listAll() {
        List<ProcessDefinitionDTO> list = workflowDefinitionService.listAllProcessDefinitions();
        return Result.success(list);
    }

    @Operation(summary = "根据Key查询流程定义")
    @GetMapping("/key/{processKey}")
    public Result<ProcessDefinitionDTO> getByKey(@PathVariable String processKey) {
        ProcessDefinitionDTO dto = workflowDefinitionService.getProcessDefinitionByKey(processKey);
        return Result.success(dto);
    }

    @Operation(summary = "查询流程定义版本列表")
    @GetMapping("/versions/{processKey}")
    public Result<List<ProcessDefinitionDTO>> listVersions(@PathVariable String processKey) {
        List<ProcessDefinitionDTO> list = workflowDefinitionService.listProcessVersions(processKey);
        return Result.success(list);
    }

    @Operation(summary = "获取流程定义XML")
    @GetMapping("/xml/{processDefinitionId}")
    public Result<String> getXml(@PathVariable String processDefinitionId) {
        String xml = workflowDefinitionService.getProcessDefinitionXml(processDefinitionId);
        return Result.success(xml);
    }

    @Operation(summary = "获取流程图")
    @GetMapping(value = "/diagram/{processDefinitionId}", produces = "image/png")
    public byte[] getDiagram(@PathVariable String processDefinitionId) {
        return workflowDefinitionService.getProcessDiagram(processDefinitionId);
    }
}
