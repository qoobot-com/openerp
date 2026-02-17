package com.qoobot.qooerp.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.common.util.StringUtils;
import com.qoobot.qooerp.file.adapter.StorageAdapter;
import com.qoobot.qooerp.file.dto.*;
import com.qoobot.qooerp.file.entity.FileInfo;
import com.qoobot.qooerp.file.mapper.FileInfoMapper;
import com.qoobot.qooerp.file.service.FileQuotaService;
import com.qoobot.qooerp.file.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件服务实现
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileService {

    private final StorageAdapter storageAdapter;
    private final FileQuotaService quotaService;
    private final FileLogServiceImpl logService;

    @Value("${minio.bucket-name:qooerp-files}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    private static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp", "bmp"};
    private static final long MAX_FILE_SIZE = 500 * 1024 * 1024; // 500MB

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfoDTO upload(MultipartFile file, Long folderId) throws IOException {
        // 校验文件
        validateFile(file);

        // 计算文件MD5
        String fileMd5 = calculateMd5(file);

        // 检查配额
        if (!quotaService.checkQuota(file.getSize())) {
            throw new BusinessException("存储空间不足");
        }

        // 生成文件编号
        String fileNo = generateFileNo();

        // 生成对象名称
        String objectName = generateObjectName(fileNo, file.getOriginalFilename());

        // 上传到存储
        String filePath = storageAdapter.upload(bucketName, objectName,
                file.getInputStream(), file.getContentType(), file.getSize());

        // 保存文件信息
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileNo(fileNo);
        fileInfo.setFileName(file.getOriginalFilename());
        fileInfo.setFilePath(filePath);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setFileExtension(getFileExtension(file.getOriginalFilename()));
        fileInfo.setFileMd5(fileMd5);
        fileInfo.setStorageType("minio");
        fileInfo.setFolderId(folderId);
        fileInfo.setStatus(1);
        fileInfo.setDownloadCount(0);
        fileInfo.setTenantId(TenantContextHolder.getTenantId());
        fileInfo.setCreatorId(getCurrentUserId());
        save(fileInfo);

        // 更新配额
        quotaService.updateUsedQuota(file.getSize());

        // 记录日志
        logService.log(fileInfo.getId(), "upload", "上传文件: " + file.getOriginalFilename());

        return convertToDTO(fileInfo);
    }

    @Override
    public List<FileInfoDTO> batchUpload(List<MultipartFile> files, Long folderId) throws IOException {
        List<FileInfoDTO> result = new ArrayList<>();
        for (MultipartFile file : files) {
            result.add(upload(file, folderId));
        }
        return result;
    }

    @Override
    public String initChunkUpload(ChunkUploadInitDTO dto) {
        // 检查文件是否已存在（秒传）
        FileInfo existFile = baseMapper.selectByMd5(dto.getFileMd5(), TenantContextHolder.getTenantId());
        if (existFile != null) {
            return "second-pass:" + existFile.getId();
        }

        // 生成任务ID
        String taskId = "chunk:" + dto.getFileMd5() + ":" + System.currentTimeMillis();

        // TODO: 将任务信息保存到Redis，记录分片上传状态
        return taskId;
    }

    @Override
    public void uploadChunk(String taskId, Integer chunkIndex, MultipartFile file) throws IOException {
        // TODO: 实现分片上传逻辑
        // 1. 上传分片到临时存储
        // 2. 记录分片上传状态
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfoDTO completeChunkUpload(ChunkUploadCompleteDTO dto) throws IOException {
        // TODO: 实现分片合并逻辑
        // 1. 合并所有分片
        // 2. 保存文件信息
        // 3. 删除临时分片
        throw new BusinessException("分片上传功能待实现");
    }

    @Override
    public FileInfoDTO secondPassUpload(String fileMd5) {
        FileInfo fileInfo = baseMapper.selectByMd5(fileMd5, TenantContextHolder.getTenantId());
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        // 复制文件记录（秒传）
        FileInfo newFile = new FileInfo();
        BeanUtils.copyPropertiesIgnoreNull(fileInfo, newFile);
        newFile.setId(null);
        newFile.setFileNo(generateFileNo());
        newFile.setDownloadCount(0);
        newFile.setCreateTime(LocalDateTime.now());
        save(newFile);

        logService.log(newFile.getId(), "second-pass", "秒传文件: " + newFile.getFileName());

        return convertToDTO(newFile);
    }

    @Override
    public void download(Long id, HttpServletResponse response) throws IOException {
        FileInfo fileInfo = getById(id);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        InputStream inputStream = storageAdapter.download(fileInfo.getFilePath());
        response.setContentType(fileInfo.getFileType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
                new String(fileInfo.getFileName().getBytes("UTF-8"), "ISO-8859-1") + "\"");
        inputStream.transferTo(response.getOutputStream());

        // 更新下载次数
        fileInfo.setDownloadCount(fileInfo.getDownloadCount() + 1);
        updateById(fileInfo);

        logService.log(id, "download", "下载文件: " + fileInfo.getFileName());
    }

    @Override
    public void batchDownload(String ids, HttpServletResponse response) throws IOException {
        // TODO: 实现批量打包下载
        throw new BusinessException("批量下载功能待实现");
    }

    @Override
    public void preview(Long id, HttpServletResponse response) throws IOException {
        FileInfo fileInfo = getById(id);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        if (!isPreviewable(fileInfo.getFileExtension())) {
            throw new BusinessException("该文件类型不支持在线预览");
        }

        InputStream inputStream = storageAdapter.download(fileInfo.getFilePath());
        response.setContentType(fileInfo.getFileType());
        inputStream.transferTo(response.getOutputStream());

        logService.log(id, "preview", "预览文件: " + fileInfo.getFileName());
    }

    @Override
    public void thumbnail(Long id, Integer width, Integer height, HttpServletResponse response) throws IOException {
        // TODO: 实现缩略图生成
        throw new BusinessException("缩略图功能待实现");
    }

    @Override
    public PageResult<FileInfoDTO> list(FileQueryDTO dto) {
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getTenantId, TenantContextHolder.getTenantId());
        wrapper.eq(dto.getFolderId() != null, FileInfo::getFolderId, dto.getFolderId());
        wrapper.like(StringUtils.isNotBlank(dto.getFileName()), FileInfo::getFileName, dto.getFileName());
        wrapper.eq(StringUtils.isNotBlank(dto.getFileType()), FileInfo::getFileType, dto.getFileType());
        wrapper.eq(StringUtils.isNotBlank(dto.getFileExtension()), FileInfo::getFileExtension, dto.getFileExtension());
        wrapper.orderByDesc(FileInfo::getCreateTime);

        Page<FileInfo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<FileInfo> result = page(page, wrapper);

        List<FileInfoDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), dtoList);
    }

    @Override
    public FileInfoDTO getInfo(Long id) {
        FileInfo fileInfo = getById(id);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }
        return convertToDTO(fileInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rename(Long id, FileRenameDTO dto) {
        FileInfo fileInfo = getById(id);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        String oldName = fileInfo.getFileName();
        fileInfo.setFileName(dto.getNewFileName());
        fileInfo.setFileExtension(getFileExtension(dto.getNewFileName()));
        updateById(fileInfo);

        logService.log(id, "rename", "重命名文件: " + oldName + " -> " + dto.getNewFileName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        FileInfo fileInfo = getById(id);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        // 删除存储文件
        storageAdapter.delete(fileInfo.getFilePath());

        // 删除记录
        removeById(id);

        // 更新配额
        quotaService.updateUsedQuota(-fileInfo.getFileSize());

        logService.log(id, "delete", "删除文件: " + fileInfo.getFileName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(String ids) {
        List<String> idList = StringUtils.splitToList(ids, ",");
        for (String id : idList) {
            delete(Long.valueOf(id));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void move(Long id, FileMoveDTO dto) {
        FileInfo fileInfo = getById(id);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        fileInfo.setFolderId(dto.getTargetFolderId());
        updateById(fileInfo);

        logService.log(id, "move", "移动文件到文件夹: " + dto.getTargetFolderId());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小超过限制（最大500MB）");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (StringUtils.isBlank(extension)) {
            throw new BusinessException("文件格式不正确");
        }
    }

    private String calculateMd5(MultipartFile file) {
        // TODO: 实现MD5计算
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateFileNo() {
        return "FILE" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + (int)(Math.random() * 1000);
    }

    private String generateObjectName(String fileNo, String fileName) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return datePath + "/" + fileNo + "." + getFileExtension(fileName);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isPreviewable(String extension) {
        return contains(ALLOWED_IMAGE_TYPES, extension);
    }

    private boolean contains(String[] array, String value) {
        for (String item : array) {
            if (item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private Long getCurrentUserId() {
        // TODO: 从Security上下文获取当前用户ID
        return 1L;
    }

    private FileInfoDTO convertToDTO(FileInfo fileInfo) {
        FileInfoDTO dto = BeanUtils.copyBean(fileInfo, FileInfoDTO.class);
        dto.setFileUrl(minioEndpoint + "/" + bucketName + "/" + fileInfo.getFilePath());
        return dto;
    }
}
