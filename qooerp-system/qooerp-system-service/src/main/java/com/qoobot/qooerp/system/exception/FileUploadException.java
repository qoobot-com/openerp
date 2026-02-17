package com.qoobot.qooerp.system.exception;

/**
 * 文件上传异常
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public class FileUploadException extends SystemException {

    public FileUploadException(String errorMessage) {
        super("FILE_UPLOAD_ERROR", errorMessage);
    }

    public FileUploadException(String errorMessage, Throwable cause) {
        super("FILE_UPLOAD_ERROR", errorMessage, cause);
    }

    /**
     * 文件大小超限异常
     */
    public static class FileSizeExceededException extends FileUploadException {
        public FileSizeExceededException(long maxSize) {
            super(String.format("文件大小超过限制，最大允许 %d MB", maxSize / (1024 * 1024)));
        }
    }

    /**
     * 文件类型不支持异常
     */
    public static class FileTypeNotSupportedException extends FileUploadException {
        public FileTypeNotSupportedException(String fileType) {
            super(String.format("不支持的文件类型: %s", fileType));
        }
    }

    /**
     * 文件为空异常
     */
    public static class EmptyFileException extends FileUploadException {
        public EmptyFileException() {
            super("上传的文件为空");
        }
    }
}
