package com.qoobot.qooerp.file.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.file.dto.FileShareCreateDTO;
import com.qoobot.qooerp.file.dto.FileShareDTO;
import com.qoobot.qooerp.file.dto.FileInfoDTO;
import com.qoobot.qooerp.file.service.FileShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件分享控制器
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Tag(name = "文件分享", description = "文件分享创建、访问等接口")
@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class FileShareController {

    private final FileShareService shareService;

    @Operation(summary = "创建分享链接")
    @PostMapping("/{fileId}")
    public Result<FileShareDTO> createShare(@PathVariable Long fileId,
                                            @RequestBody @Valid FileShareCreateDTO dto) {
        return Result.success(shareService.createShare(fileId, dto));
    }

    @Operation(summary = "访问分享链接")
    @GetMapping("/{code}")
    public Result<FileInfoDTO> accessShare(@PathVariable String code,
                                            @RequestParam(value = "password", required = false) String password) {
        return Result.success(shareService.accessShare(code, password));
    }

    @Operation(summary = "获取分享信息")
    @GetMapping("/{code}/info")
    public Result<FileShareDTO> getShareInfo(@PathVariable String code) {
        return Result.success(shareService.getShareInfo(code));
    }

    @Operation(summary = "取消分享")
    @DeleteMapping("/{id}")
    public Result<Void> cancelShare(@PathVariable Long id) {
        shareService.cancelShare(id);
        return Result.success();
    }

    @Operation(summary = "文件的分享列表")
    @GetMapping("/list")
    public Result<List<FileShareDTO>> listShares(@RequestParam Long fileId) {
        return Result.success(shareService.listShares(fileId));
    }
}
