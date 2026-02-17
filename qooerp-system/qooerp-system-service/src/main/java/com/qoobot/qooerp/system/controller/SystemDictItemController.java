package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.dto.SystemDictItemDTO;
import com.qoobot.qooerp.system.service.SystemDictItemService;
import com.qoobot.qooerp.system.vo.SystemDictItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典项管理Controller
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Tag(name = "字典项管理", description = "字典项管理接口")
@RestController
@RequestMapping("/api/system/dict-item")
@RequiredArgsConstructor
public class SystemDictItemController {

    private final SystemDictItemService dictItemService;

    @Operation(summary = "创建字典项")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SystemDictItemDTO dto) {
        Long id = dictItemService.createDictItem(dto);
        return Result.success(id);
    }

    @Operation(summary = "获取字典项详情")
    @GetMapping("/{id}")
    public Result<SystemDictItemVO> getById(@Parameter(description = "字典项ID") @PathVariable Long id) {
        SystemDictItemVO vo = dictItemService.getDictItem(id);
        return Result.success(vo);
    }

    @Operation(summary = "更新字典项")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "字典项ID") @PathVariable Long id,
                                @Valid @RequestBody SystemDictItemDTO dto) {
        dictItemService.updateDictItem(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除字典项")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "字典项ID") @PathVariable Long id) {
        dictItemService.deleteDictItem(id);
        return Result.success();
    }

    @Operation(summary = "分页查询字典项")
    @GetMapping("/list")
    public Result<Page<SystemDictItemVO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "字典ID") @RequestParam(required = false) Long dictId,
            @Parameter(description = "字典标签") @RequestParam(required = false) String itemLabel) {
        Page<SystemDictItemVO> page = dictItemService.pageDictItem(current, size, dictId, itemLabel);
        return Result.success(page);
    }

    @Operation(summary = "根据字典ID获取字典项列表")
    @GetMapping("/dict/{dictId}")
    public Result<List<SystemDictItemVO>> getItemsByDictId(@Parameter(description = "字典ID") @PathVariable Long dictId) {
        List<SystemDictItemVO> list = dictItemService.getItemsByDictId(dictId);
        return Result.success(list);
    }

    @Operation(summary = "根据字典编码获取字典项列表")
    @GetMapping("/dict/code/{dictCode}")
    public Result<List<SystemDictItemVO>> getItemsByDictCode(@Parameter(description = "字典编码") @PathVariable String dictCode) {
        List<SystemDictItemVO> list = dictItemService.getItemsByDictCode(dictCode);
        return Result.success(list);
    }
}
