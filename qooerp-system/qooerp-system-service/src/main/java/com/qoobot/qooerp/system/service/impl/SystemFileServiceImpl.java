package com.qoobot.qooerp.system.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.system.entity.SystemFile;
import com.qoobot.qooerp.system.mapper.SystemFileMapper;
import com.qoobot.qooerp.system.service.SystemFileService;
import com.qoobot.qooerp.system.vo.SystemFileVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件服务实现
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemFileServiceImpl extends ServiceImpl<SystemFileMapper, SystemFile> implements SystemFileService {

    @Value("${file.upload.path:/data/qooerp/files}")
    private String uploadPath;

    @Value("${file.access.url:/file}")
    private String accessUrl;

    @Value("${file.upload.max-size:10485760}")
    private Long maxFileSize;

    private static final String[] ALLOWED_EXTS = {"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx"};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemFileVO uploadFile(MultipartFile file, String module) throws IOException {
        // 验证文件
        validateFile(file);

        // 生成文件路径
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "." + fileExt;
        String relativePath = module + "/" + datePath + "/" + fileName;
        String fullPath = Paths.get(uploadPath, relativePath).toString();

        // 确保目录存在
        Path targetPath = Paths.get(fullPath);
        Files.createDirectories(targetPath.getParent());

        // 保存文件
        file.transferTo(targetPath.toFile());

        // 计算MD5
        String md5 = DigestUtil.md5Hex(file.getInputStream());

        // 保存文件记录
        SystemFile systemFile = new SystemFile();
        systemFile.setFileName(fileName);
        systemFile.setOriginalName(file.getOriginalFilename());
        systemFile.setFileExt(fileExt);
        systemFile.setFileSize(file.getSize());
        systemFile.setMimeType(file.getContentType());
        systemFile.setStorageType("local");
        systemFile.setFilePath(relativePath);
        systemFile.setFileUrl(accessUrl + "/" + relativePath);
        systemFile.setMd5(md5);
        systemFile.setUploader(SecurityUtils.getUsernameOrDefault("system"));

        baseMapper.insert(systemFile);

        SystemFileVO vo = new SystemFileVO();
        vo.setId(systemFile.getId());
        vo.setFileName(systemFile.getFileName());
        vo.setOriginalName(systemFile.getOriginalName());
        vo.setFileExt(systemFile.getFileExt());
        vo.setFileSize(systemFile.getFileSize());
        vo.setMimeType(systemFile.getMimeType());
        vo.setStorageType(systemFile.getStorageType());
        vo.setFilePath(systemFile.getFilePath());
        vo.setFileUrl(systemFile.getFileUrl());
        vo.setMd5(systemFile.getMd5());
        vo.setUploader(systemFile.getUploader());
        vo.setCreateTime(systemFile.getCreateTime());

        return vo;
    }

    @Override
    public SystemFileVO getFile(Long id) {
        SystemFile file = baseMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        SystemFileVO vo = new SystemFileVO();
        vo.setId(file.getId());
        vo.setFileName(file.getFileName());
        vo.setOriginalName(file.getOriginalName());
        vo.setFileExt(file.getFileExt());
        vo.setFileSize(file.getFileSize());
        vo.setMimeType(file.getMimeType());
        vo.setStorageType(file.getStorageType());
        vo.setFilePath(file.getFilePath());
        vo.setFileUrl(file.getFileUrl());
        vo.setMd5(file.getMd5());
        vo.setUploader(file.getUploader());
        vo.setCreateTime(file.getCreateTime());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long id) {
        SystemFile file = baseMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        // 删除物理文件
        try {
            String fullPath = Paths.get(uploadPath, file.getFilePath()).toString();
            FileUtil.del(fullPath);
        } catch (Exception e) {
            log.warn("删除物理文件失败: {}", file.getFilePath(), e);
        }

        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public void downloadFile(Long id, HttpServletResponse response) throws IOException {
        SystemFile file = baseMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        String fullPath = Paths.get(uploadPath, file.getFilePath()).toString();
        Path path = Paths.get(fullPath);

        if (!Files.exists(path)) {
            throw new BusinessException("文件不存在");
        }

        response.setContentType(file.getMimeType());
        response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + new String(file.getOriginalName().getBytes("UTF-8"), "ISO-8859-1") + "\"");
        response.setContentLengthLong(file.getFileSize());

        Files.copy(path, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @Override
    public void previewFile(Long id, HttpServletResponse response) throws IOException {
        SystemFile file = baseMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        String fullPath = Paths.get(uploadPath, file.getFilePath()).toString();
        Path path = Paths.get(fullPath);

        if (!Files.exists(path)) {
            throw new BusinessException("文件不存在");
        }

        response.setContentType(file.getMimeType());
        response.setContentLengthLong(file.getFileSize());

        Files.copy(path, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @Override
    public Page<SystemFileVO> pageFile(Long current, Long size, String fileName, String fileExt, String uploader) {
        Page<SystemFile> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemFile> wrapper = new LambdaQueryWrapper<>();

        if (fileName != null && !fileName.isEmpty()) {
            wrapper.like(SystemFile::getOriginalName, fileName);
        }
        if (fileExt != null && !fileExt.isEmpty()) {
            wrapper.eq(SystemFile::getFileExt, fileExt);
        }
        if (uploader != null && !uploader.isEmpty()) {
            wrapper.eq(SystemFile::getUploader, uploader);
        }

        wrapper.orderByDesc(SystemFile::getCreateTime);

        Page<SystemFile> resultPage = baseMapper.selectPage(page, wrapper);

        Page<SystemFileVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<SystemFileVO> voList = resultPage.getRecords().stream().map(file -> {
            SystemFileVO vo = new SystemFileVO();
            vo.setId(file.getId());
            vo.setFileName(file.getFileName());
            vo.setOriginalName(file.getOriginalName());
            vo.setFileExt(file.getFileExt());
            vo.setFileSize(file.getFileSize());
            vo.setMimeType(file.getMimeType());
            vo.setStorageType(file.getStorageType());
            vo.setFilePath(file.getFilePath());
            vo.setFileUrl(file.getFileUrl());
            vo.setMd5(file.getMd5());
            vo.setUploader(file.getUploader());
            vo.setCreateTime(file.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("文件大小不能超过 " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型
        String fileExt = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        boolean isAllowed = false;
        for (String ext : ALLOWED_EXTS) {
            if (ext.equalsIgnoreCase(fileExt)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw new BusinessException("不支持的文件类型: " + fileExt);
        }
    }
}
