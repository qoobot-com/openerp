package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.dto.SystemDictDTO;
import com.qoobot.qooerp.system.service.SystemDictService;
import com.qoobot.qooerp.system.vo.SystemDictVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理Controller
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Tag(name = "字典管理", description = "字典管理接口")
@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class SystemDictController {

    private final SystemDictService dictService;

    @Operation(summary = "创建字典")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SystemDictDTO dto) {
        Long id = dictService.createDict(dto);
        return Result.success(id);
    }

    @Operation(summary = "获取字典详情")
    @GetMapping("/{id}")
    public Result<SystemDictVO> getById(@Parameter(description = "字典ID") @PathVariable Long id) {
        SystemDictVO vo = dictService.getDict(id);
        return Result.success(vo);
    }

    @Operation(summary = "根据编码获取字典")
    @GetMapping("/code/{code}")
    public Result<SystemDictVO> getByCode(@Parameter(description = "字典编码") @PathVariable String code) {
        SystemDictVO vo = dictService.getDictByCode(code);
        return Result.success(vo);
    }

    @Operation(summary = "更新字典")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "字典ID") @PathVariable Long id,
                                @Valid @RequestBody SystemDictDTO dto) {
        dictService.updateDict(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除字典")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "字典ID") @PathVariable Long id) {
        dictService.deleteDict(id);
        return Result.success();
    }

    @Operation(summary = "分页查询字典")
    @GetMapping("/list")
    public Result<Page<SystemDictVO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "字典编码") @RequestParam(required = false) String dictCode,
            @Parameter(description = "字典名称") @RequestParam(required = false) String dictName,
            @Parameter(description = "字典类型") @RequestParam(required = false) String dictType) {
        Page<SystemDictVO> page = dictService.pageDict(current, size, dictCode, dictName, dictType);
        return Result.success(page);
    }

    @Operation(summary = "获取所有字典")
    @GetMapping("/all")
    public Result<List<SystemDictVO>> getAll() {
        List<SystemDictVO> list = dictService.getAllDicts();
        return Result.success(list);
    }
}
