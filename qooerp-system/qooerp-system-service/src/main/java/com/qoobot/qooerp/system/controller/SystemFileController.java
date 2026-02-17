package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.service.SystemFileService;
import com.qoobot.qooerp.system.vo.SystemFileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件管理Controller
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Tag(name = "文件管理", description = "文件管理接口")
@RestController
@RequestMapping("/api/system/file")
@RequiredArgsConstructor
public class SystemFileController {

    private final SystemFileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<SystemFileVO> upload(@Parameter(description = "文件") @RequestParam("file") MultipartFile file,
                                       @Parameter(description = "模块名") @RequestParam(defaultValue = "common") String module) throws IOException {
        SystemFileVO vo = fileService.uploadFile(file, module);
        return Result.success(vo);
    }

    @Operation(summary = "获取文件信息")
    @GetMapping("/{id}")
    public Result<SystemFileVO> getById(@Parameter(description = "文件ID") @PathVariable Long id) {
        SystemFileVO vo = fileService.getFile(id);
        return Result.success(vo);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "文件ID") @PathVariable Long id) {
        fileService.deleteFile(id);
        return Result.success();
    }

    @Operation(summary = "下载文件")
    @GetMapping("/{id}/download")
    public void download(@Parameter(description = "文件ID") @PathVariable Long id,
                          HttpServletResponse response) throws IOException {
        fileService.downloadFile(id, response);
    }

    @Operation(summary = "预览文件")
    @GetMapping("/{id}/preview")
    public void preview(@Parameter(description = "文件ID") @PathVariable Long id,
                         HttpServletResponse response) throws IOException {
        fileService.previewFile(id, response);
    }

    @Operation(summary = "分页查询文件")
    @GetMapping("/list")
    public Result<Page<SystemFileVO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "文件名") @RequestParam(required = false) String fileName,
            @Parameter(description = "文件扩展名") @RequestParam(required = false) String fileExt,
            @Parameter(description = "上传人") @RequestParam(required = false) String uploader) {
        Page<SystemFileVO> page = fileService.pageFile(current, size, fileName, fileExt, uploader);
        return Result.success(page);
    }
}
