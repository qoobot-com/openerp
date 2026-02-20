package com.qoobot.qooerp.file.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.file.dto.*;
import com.qoobot.qooerp.file.service.FileService;
import com.qoobot.qooerp.file.service.FileQuotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件管理控制器
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Slf4j
@Tag(name = "文件管理", description = "文件上传、下载、预览等接口")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileQuotaService quotaService;

    @Operation(summary = "单文件上传")
    @PostMapping("/upload")
    public Result<FileInfoDTO> upload(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "folderId", required = false) Long folderId) throws IOException {
        return Result.success(fileService.upload(file, folderId));
    }

    @Operation(summary = "批量文件上传")
    @PostMapping("/upload/batch")
    public Result<List<FileInfoDTO>> batchUpload(@RequestParam("files") List<MultipartFile> files,
                                                   @RequestParam(value = "folderId", required = false) Long folderId) throws IOException {
        return Result.success(fileService.batchUpload(files, folderId));
    }

    @Operation(summary = "初始化分片上传")
    @PostMapping("/upload/chunk/init")
    public Result<String> initChunkUpload(@RequestBody @Valid ChunkUploadInitDTO dto) {
        return Result.success(fileService.initChunkUpload(dto));
    }

    @Operation(summary = "上传文件分片")
    @PostMapping("/upload/chunk")
    public Result<Void> uploadChunk(@RequestParam("taskId") String taskId,
                                     @RequestParam("chunkIndex") Integer chunkIndex,
                                     @RequestParam("file") MultipartFile file) throws IOException {
        fileService.uploadChunk(taskId, chunkIndex, file);
        return Result.success();
    }

    @Operation(summary = "完成分片上传")
    @PostMapping("/upload/chunk/complete")
    public Result<FileInfoDTO> completeChunkUpload(@RequestBody @Valid ChunkUploadCompleteDTO dto) throws IOException {
        return Result.success(fileService.completeChunkUpload(dto));
    }

    @Operation(summary = "文件秒传")
    @PostMapping("/upload/second-pass")
    public Result<FileInfoDTO> secondPassUpload(@RequestParam("fileMd5") String fileMd5) {
        return Result.success(fileService.secondPassUpload(fileMd5));
    }

    @Operation(summary = "文件下载")
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        fileService.download(id, response);
    }

    @Operation(summary = "批量下载")
    @GetMapping("/download/batch")
    public void batchDownload(@RequestParam("ids") String ids, HttpServletResponse response) throws IOException {
        fileService.batchDownload(ids, response);
    }

    @Operation(summary = "文件预览")
    @GetMapping("/preview/{id}")
    public void preview(@PathVariable Long id, HttpServletResponse response) throws IOException {
        fileService.preview(id, response);
    }

    @Operation(summary = "获取缩略图")
    @GetMapping("/preview/{id}/thumbnail")
    public void thumbnail(@PathVariable Long id,
                           @RequestParam(required = false) Integer width,
                           @RequestParam(required = false) Integer height,
                           HttpServletResponse response) throws IOException {
        fileService.thumbnail(id, width, height, response);
    }

    @Operation(summary = "文件列表查询")
    @GetMapping("/list")
    public Result<PageResult<FileInfoDTO>> list(FileQueryDTO dto) {
        return Result.success(fileService.list(dto));
    }

    @Operation(summary = "获取文件详情")
    @GetMapping("/{id}")
    public Result<FileInfoDTO> getInfo(@PathVariable Long id) {
        return Result.success(fileService.getInfo(id));
    }

    @Operation(summary = "文件重命名")
    @PutMapping("/{id}/rename")
    public Result<Void> rename(@PathVariable Long id, @RequestBody @Valid FileRenameDTO dto) {
        fileService.rename(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除文件")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestParam("ids") String ids) {
        fileService.batchDelete(ids);
        return Result.success();
    }

    @Operation(summary = "移动文件")
    @PutMapping("/{id}/move")
    public Result<Void> move(@PathVariable Long id, @RequestBody @Valid FileMoveDTO dto) {
        fileService.move(id, dto);
        return Result.success();
    }

    @Operation(summary = "获取存储配额信息")
    @GetMapping("/quota/info")
    public Result<FileQuotaDTO> getQuotaInfo() {
        return Result.success(quotaService.getQuotaInfo());
    }

    @Operation(summary = "获取存储统计")
    @GetMapping("/storage/statistics")
    public Result<StorageStatisticsDTO> getStatistics() {
        return Result.success(quotaService.getStatistics());
    }
}
