package com.qoobot.qooerp.system.constants;

/**
 * 系统模块常量定义
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public class SystemConstants {

    /**
     * 缓存名称
     */
    public static class Cache {
        /** 字典缓存 */
        public static final String DICT = "system:dict";

        /** 字典项缓存 */
        public static final String DICT_ITEM = "system:dict:item";

        /** 配置缓存 */
        public static final String CONFIG = "system:config";

        /** 所有字典缓存 */
        public static final String ALL_DICTS = "system:dict:all";
    }

    /**
     * 操作类型
     */
    public static class OperationType {
        /** 查询 */
        public static final String QUERY = "查询";

        /** 新增 */
        public static final String CREATE = "新增";

        /** 修改 */
        public static final String UPDATE = "修改";

        /** 删除 */
        public static final String DELETE = "删除";

        /** 导出 */
        public static final String EXPORT = "导出";

        /** 导入 */
        public static final String IMPORT = "导入";
    }

    /**
     * 文件上传
     */
    public static class FileUpload {
        /** 默认上传路径 */
        public static final String DEFAULT_UPLOAD_PATH = "/upload";

        /** 允许的图片类型 */
        public static final String[] IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};

        /** 允许的文档类型 */
        public static final String[] DOCUMENT_TYPES = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"};

        /** 允许的压缩包类型 */
        public static final String[] ARCHIVE_TYPES = {"zip", "rar", "7z", "tar", "gz"};

        /** 最大文件大小（字节） - 默认100MB */
        public static final long MAX_FILE_SIZE = 100 * 1024 * 1024;
    }

    /**
     * 定时任务
     */
    public static class Job {
        /** 默认任务组 */
        public static final String DEFAULT_GROUP = "DEFAULT";

        /** 错过执行策略 - 立即执行 */
        public static final int MISFIRE_POLICY_EXECUTE = 1;

        /** 错过执行策略 - 执行一次 */
        public static final int MISFIRE_POLICY_ONCE = 2;

        /** 错过执行策略 - 放弃执行 */
        public static final int MISFIRE_POLICY_ABANDON = 3;
    }

    /**
     * 字典编码常量
     */
    public static class DictCode {
        /** 用户状态 */
        public static final String USER_STATUS = "user_status";

        /** 字典状态 */
        public static final String DICT_STATUS = "dict_status";

        /** 任务状态 */
        public static final String JOB_STATUS = "job_status";

        /** 系统状态 */
        public static final String SYS_STATUS = "sys_status";
    }

    /**
     * 系统配置键常量
     */
    public static class ConfigKey {
        /** 系统名称 */
        public static final String SYS_NAME = "sys.name";

        /** 系统版本 */
        public static final String SYS_VERSION = "sys.version";

        /** 会话超时时间 */
        public static final String SESSION_TIMEOUT = "sys.session.timeout";

        /** 最大上传文件大小 */
        public static final String UPLOAD_MAX_SIZE = "sys.upload.maxSize";

        /** 允许的文件类型 */
        public static final String UPLOAD_ALLOWED_TYPES = "sys.upload.allowedTypes";

        /** 系统维护模式 */
        public static final String MAINTENANCE_MODE = "sys.maintenance";
    }

    private SystemConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
