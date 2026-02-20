package com.qoobot.qooerp.file.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.file.dto.FileFolderCreateDTO;
import com.qoobot.qooerp.file.dto.FileFolderDTO;
import com.qoobot.qooerp.file.dto.FileFolderUpdateDTO;
import com.qoobot.qooerp.file.service.FileFolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件夹管理控制器
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Tag(name = "文件夹管理", description = "文件夹创建、查询等接口")
@RestController
@RequestMapping("/api/folder")
@RequiredArgsConstructor
public class FileFolderController {

    private final FileFolderService folderService;

    @Operation(summary = "创建文件夹")
    @PostMapping
    public Result<FileFolderDTO> create(@RequestBody @Valid FileFolderCreateDTO dto) {
        return Result.success(folderService.create(dto));
    }

    @Operation(summary = "更新文件夹")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid FileFolderUpdateDTO dto) {
        folderService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除文件夹")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        folderService.delete(id);
        return Result.success();
    }

    @Operation(summary = "文件夹列表")
    @GetMapping("/list")
    public Result<List<FileFolderDTO>> list(@RequestParam(value = "parentId", defaultValue = "0") Long parentId) {
        return Result.success(folderService.list(parentId));
    }

    @Operation(summary = "文件夹树")
    @GetMapping("/{id}/tree")
    public Result<List<FileFolderDTO>> getTree(@PathVariable Long id) {
        return Result.success(folderService.getTree(id));
    }
}
