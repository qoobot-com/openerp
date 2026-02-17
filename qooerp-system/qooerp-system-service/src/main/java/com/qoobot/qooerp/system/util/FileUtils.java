package com.qoobot.qooerp.system.util;

import com.qoobot.qooerp.system.constants.SystemConstants;
import com.qoobot.qooerp.system.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件工具类
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
public class FileUtils {

    /**
     * 日期时间格式
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * 检查文件类型是否允许
     *
     * @param filename 文件名
     * @param allowedTypes 允许的文件类型数组
     * @return 是否允许
     */
    public static boolean isAllowedFileType(String filename, String[] allowedTypes) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        return Arrays.asList(allowedTypes).contains(extension);
    }

    /**
     * 检查文件大小是否超限
     *
     * @param file 文件
     * @param maxSize 最大文件大小（字节）
     * @return 是否超限
     */
    public static boolean isFileSizeExceeded(MultipartFile file, long maxSize) {
        return file.getSize() > maxSize;
    }

    /**
     * 生成新的文件名
     *
     * @param originalFilename 原始文件名
     * @return 新文件名
     */
    public static String generateNewFilename(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * 生成日期路径
     *
     * @return 日期路径，如 2024/02/17
     */
    public static String generateDatePath() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    /**
     * 保存上传的文件
     *
     * @param file 上传的文件
     * @param uploadPath 上传基础路径
     * @return 文件相对路径
     * @throws IOException IO异常
     */
    public static String saveUploadedFile(MultipartFile file, String uploadPath) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new FileUploadException.EmptyFileException();
        }

        // 检查文件大小
        if (isFileSizeExceeded(file, SystemConstants.FileUpload.MAX_FILE_SIZE)) {
            throw new FileUploadException.FileSizeExceededException(SystemConstants.FileUpload.MAX_FILE_SIZE);
        }

        // 检查文件类型
        String filename = file.getOriginalFilename();
        if (!isAllowedFileType(filename, getAllowedFileTypes())) {
            throw new FileUploadException.FileTypeNotSupportedException(FilenameUtils.getExtension(filename));
        }

        // 生成新文件名和路径
        String newFilename = generateNewFilename(filename);
        String datePath = generateDatePath();
        String relativePath = datePath + "/" + newFilename;
        String fullPath = uploadPath + "/" + relativePath;

        // 创建目录
        Path path = Paths.get(fullPath).getParent();
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // 保存文件
        file.transferTo(new File(fullPath));

        log.info("文件上传成功: {}", fullPath);
        return relativePath;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath, String basePath) {
        try {
            File file = new File(basePath + "/" + filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                log.info("文件删除: {}, 结果: {}", filePath, deleted ? "成功" : "失败");
                return deleted;
            }
            return false;
        } catch (Exception e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取所有允许的文件类型
     *
     * @return 文件类型数组
     */
    private static String[] getAllowedFileTypes() {
        String[] types = new String[
            SystemConstants.FileUpload.IMAGE_TYPES.length +
            SystemConstants.FileUpload.DOCUMENT_TYPES.length +
            SystemConstants.FileUpload.ARCHIVE_TYPES.length
        ];

        System.arraycopy(SystemConstants.FileUpload.IMAGE_TYPES, 0, types, 0, SystemConstants.FileUpload.IMAGE_TYPES.length);
        System.arraycopy(SystemConstants.FileUpload.DOCUMENT_TYPES, 0,
                types, SystemConstants.FileUpload.IMAGE_TYPES.length, SystemConstants.FileUpload.DOCUMENT_TYPES.length);
        System.arraycopy(SystemConstants.FileUpload.ARCHIVE_TYPES, 0,
                types, SystemConstants.FileUpload.IMAGE_TYPES.length + SystemConstants.FileUpload.DOCUMENT_TYPES.length,
                SystemConstants.FileUpload.ARCHIVE_TYPES.length);

        return types;
    }

    /**
     * 获取文件大小友好显示
     *
     * @param size 文件大小（字节）
     * @return 友好显示的文件大小
     */
    public static String getHumanReadableSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    private FileUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
