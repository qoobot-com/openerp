package com.qoobot.qooerp.system.util;

import com.qoobot.qooerp.system.config.SystemProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件工具类
 */
@Component
@RequiredArgsConstructor
public class FileUtils {

    private final SystemProperties systemProperties;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String module) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        // 验证文件扩展名
        if (!isExtensionAllowed(extension)) {
            throw new RuntimeException("不支持的文件类型: " + extension);
        }

        // 验证文件大小
        if (file.getSize() > getMaxFileSizeBytes()) {
            throw new RuntimeException("文件大小超过限制");
        }

        // 生成文件名
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString() + "." + extension;

        // 构建保存路径
        String uploadPath = getUploadPath();
        String relativePath = module + "/" + datePath + "/" + fileName;
        String fullPath = uploadPath + File.separator + relativePath;

        // 确保目录存在
        Path filePath = Paths.get(fullPath);
        Files.createDirectories(filePath.getParent());

        // 保存文件
        file.transferTo(filePath.toFile());

        return relativePath.replace("\\", "/");
    }

    /**
     * 删除文件
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }

        String fullPath = getUploadPath() + File.separator + relativePath;
        Path filePath = Paths.get(fullPath);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 忽略删除失败
        }
    }

    /**
     * 获取完整文件路径
     */
    public String getFullPath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        return getUploadPath() + File.separator + relativePath.replace("/", File.separator);
    }

    /**
     * 获取文件大小（字节）
     */
    public long getFileSize(String relativePath) {
        String fullPath = getFullPath(relativePath);
        if (fullPath == null) {
            return 0;
        }

        File file = new File(fullPath);
        return file.exists() ? file.length() : 0;
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String relativePath) {
        String fullPath = getFullPath(relativePath);
        if (fullPath == null) {
            return false;
        }

        return new File(fullPath).exists();
    }

    /**
     * 检查文件扩展名是否允许
     */
    private boolean isExtensionAllowed(String extension) {
        String allowedExtensions = systemProperties.getFile().getAllowedExtensions();
        if (allowedExtensions == null || allowedExtensions.isEmpty()) {
            return true;
        }

        String[] extensions = allowedExtensions.split(",");
        for (String ext : extensions) {
            if (ext.trim().equalsIgnoreCase(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取最大文件大小（字节）
     */
    private long getMaxFileSizeBytes() {
        String maxFileSize = systemProperties.getFile().getMaxFileSize();
        if (maxFileSize == null || maxFileSize.isEmpty()) {
            return 100 * 1024 * 1024; // 默认100MB
        }

        maxFileSize = maxFileSize.toUpperCase().trim();
        if (maxFileSize.endsWith("GB")) {
            return Long.parseLong(maxFileSize.substring(0, maxFileSize.length() - 2)) * 1024 * 1024 * 1024;
        } else if (maxFileSize.endsWith("MB")) {
            return Long.parseLong(maxFileSize.substring(0, maxFileSize.length() - 2)) * 1024 * 1024;
        } else if (maxFileSize.endsWith("KB")) {
            return Long.parseLong(maxFileSize.substring(0, maxFileSize.length() - 2)) * 1024;
        } else {
            return Long.parseLong(maxFileSize);
        }
    }

    /**
     * 获取上传路径
     */
    private String getUploadPath() {
        String uploadPath = systemProperties.getFile().getUploadPath();
        if (uploadPath == null || uploadPath.isEmpty()) {
            uploadPath = System.getProperty("user.home") + File.separator + "qooerp" + File.separator + "upload";
        }

        // 确保目录存在
        Path path = Paths.get(uploadPath);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + uploadPath, e);
        }

        return uploadPath;
    }
}
