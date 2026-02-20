package com.qoobot.qooerp.lowcode.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.lowcode.entity.LowcodeForm;
import com.qoobot.qooerp.lowcode.service.LowcodeFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 低代码表单控制器
 */
@Tag(name = "低代码表单管理", description = "低代码表单管理接口")
@RestController
@RequestMapping("/lowcode/form")
@RequiredArgsConstructor
public class LowcodeFormController {

    private final LowcodeFormService lowcodeFormService;

    @Operation(summary = "查询表单列表", description = "查询所有表单")
    @GetMapping("/list")
    public Result<List<LowcodeForm>> list() {
        return Result.success(lowcodeFormService.list());
    }

    @Operation(summary = "根据ID查询表单", description = "根据ID查询单个表单")
    @GetMapping("/{id}")
    public Result<LowcodeForm> getById(@PathVariable Long id) {
        return Result.success(lowcodeFormService.getById(id));
    }

    @Operation(summary = "新增表单", description = "新增低代码表单")
    @PostMapping
    public Result<Boolean> save(@RequestBody LowcodeForm lowcodeForm) {
        return Result.success(lowcodeFormService.save(lowcodeForm));
    }

    @Operation(summary = "更新表单", description = "更新低代码表单")
    @PutMapping
    public Result<Boolean> updateById(@RequestBody LowcodeForm lowcodeForm) {
        return Result.success(lowcodeFormService.updateById(lowcodeForm));
    }

    @Operation(summary = "删除表单", description = "删除低代码表单")
    @DeleteMapping("/{id}")
    public Result<Boolean> removeById(@PathVariable Long id) {
        return Result.success(lowcodeFormService.removeById(id));
    }
}
