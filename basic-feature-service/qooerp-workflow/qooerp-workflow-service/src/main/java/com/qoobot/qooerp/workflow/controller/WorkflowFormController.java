package com.qoobot.qooerp.workflow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.dto.FormDTO;
import com.qoobot.qooerp.workflow.dto.FormDataDTO;
import com.qoobot.qooerp.workflow.dto.FormFieldDTO;
import com.qoobot.qooerp.workflow.entity.WorkflowForm;
import com.qoobot.qooerp.workflow.service.WorkflowFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程表单控制器
 */
@Tag(name = "流程表单管理", description = "流程表单的定义、字段管理、数据存储")
@RestController
@RequestMapping("/form")
@RequiredArgsConstructor
public class WorkflowFormController {

    private final WorkflowFormService workflowFormService;

    @Operation(summary = "创建表单")
    @PostMapping("/create")
    public Result<String> create(@RequestBody FormDTO formDTO) {
        String formId = workflowFormService.createForm(formDTO);
        return Result.success(formId);
    }

    @Operation(summary = "更新表单")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody FormDTO formDTO) {
        workflowFormService.updateForm(formDTO);
        return Result.success();
    }

    @Operation(summary = "删除表单")
    @DeleteMapping("/delete/{formId}")
    public Result<Void> delete(@PathVariable String formId) {
        workflowFormService.deleteForm(formId);
        return Result.success();
    }

    @Operation(summary = "发布表单")
    @PostMapping("/publish/{formId}")
    public Result<Void> publish(@PathVariable String formId) {
        workflowFormService.publishForm(formId);
        return Result.success();
    }

    @Operation(summary = "停用表单")
    @PostMapping("/unpublish/{formId}")
    public Result<Void> unpublish(@PathVariable String formId) {
        workflowFormService.unpublishForm(formId);
        return Result.success();
    }

    @Operation(summary = "复制表单")
    @PostMapping("/copy/{formId}")
    public Result<String> copy(
            @PathVariable String formId,
            @RequestParam("newName") String newName,
            @RequestParam("newCode") String newCode) {
        String newFormId = workflowFormService.copyForm(formId, newName, newCode);
        return Result.success(newFormId);
    }

    @Operation(summary = "查询表单详情")
    @GetMapping("/detail/{formId}")
    public Result<FormDTO> getDetail(@PathVariable String formId) {
        FormDTO dto = workflowFormService.getForm(formId);
        return Result.success(dto);
    }

    @Operation(summary = "根据编码查询表单")
    @GetMapping("/code/{code}")
    public Result<FormDTO> getByCode(@PathVariable String code) {
        FormDTO dto = workflowFormService.getFormByCode(code);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询表单列表")
    @GetMapping("/page")
    public Result<PageResult<WorkflowForm>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "表单名称") @RequestParam(required = false) String name,
            @Parameter(description = "表单编码") @RequestParam(required = false) String code,
            @Parameter(description = "表单状态") @RequestParam(required = false) Integer status) {
        Page<WorkflowForm> page = workflowFormService.pageForms(current, size, name, code, status);
        return Result.success(PageResult.of(page, page.getRecords()));
    }

    @Operation(summary = "查询所有表单")
    @GetMapping("/list")
    public Result<List<FormDTO>> listAll() {
        List<FormDTO> list = workflowFormService.listAllForms();
        return Result.success(list);
    }

    @Operation(summary = "添加表单字段")
    @PostMapping("/field/add")
    public Result<String> addField(@RequestBody FormFieldDTO formFieldDTO) {
        String fieldId = workflowFormService.addFormField(formFieldDTO);
        return Result.success(fieldId);
    }

    @Operation(summary = "更新表单字段")
    @PostMapping("/field/update")
    public Result<Void> updateField(@RequestBody FormFieldDTO formFieldDTO) {
        workflowFormService.updateFormField(formFieldDTO);
        return Result.success();
    }

    @Operation(summary = "删除表单字段")
    @DeleteMapping("/field/delete/{fieldId}")
    public Result<Void> deleteField(@PathVariable String fieldId) {
        workflowFormService.deleteFormField(fieldId);
        return Result.success();
    }

    @Operation(summary = "查询表单字段列表")
    @GetMapping("/field/list/{formId}")
    public Result<List<FormFieldDTO>> listFields(@PathVariable String formId) {
        List<FormFieldDTO> list = workflowFormService.listFormFields(formId);
        return Result.success(list);
    }

    @Operation(summary = "保存表单数据")
    @PostMapping("/data/save")
    public Result<String> saveData(@RequestBody FormDataDTO formDataDTO) {
        String recordId = workflowFormService.saveFormData(formDataDTO);
        return Result.success(recordId);
    }

    @Operation(summary = "更新表单数据")
    @PostMapping("/data/update/{recordId}")
    public Result<Void> updateData(
            @PathVariable String recordId,
            @RequestBody String formData) {
        workflowFormService.updateFormData(recordId, formData);
        return Result.success();
    }

    @Operation(summary = "删除表单数据")
    @DeleteMapping("/data/delete/{recordId}")
    public Result<Void> deleteData(@PathVariable String recordId) {
        workflowFormService.deleteFormData(recordId);
        return Result.success();
    }

    @Operation(summary = "查询表单数据")
    @GetMapping("/data/detail/{recordId}")
    public Result<FormDataDTO> getData(@PathVariable String recordId) {
        FormDataDTO dto = workflowFormService.getFormData(recordId);
        return Result.success(dto);
    }

    @Operation(summary = "根据业务数据查询表单数据")
    @GetMapping("/data/business")
    public Result<FormDataDTO> getDataByBusiness(
            @RequestParam String businessType,
            @RequestParam String businessId) {
        FormDataDTO dto = workflowFormService.getFormDataByBusiness(businessType, businessId);
        return Result.success(dto);
    }

    @Operation(summary = "验证表单数据")
    @PostMapping("/data/validate")
    public Result<Map<String, Object>> validateData(
            @RequestParam String formId,
            @RequestBody Map<String, Object> formData) {
        Map<String, Object> result = workflowFormService.validateFormData(formId, formData);
        return Result.success(result);
    }

    @Operation(summary = "获取表单权限配置")
    @GetMapping("/permission")
    public Result<Map<String, Object>> getPermissions(
            @RequestParam String formId,
            @RequestParam(required = false) String taskId) {
        Map<String, Object> permissions = workflowFormService.getFormPermissions(formId, taskId);
        return Result.success(permissions);
    }
}
