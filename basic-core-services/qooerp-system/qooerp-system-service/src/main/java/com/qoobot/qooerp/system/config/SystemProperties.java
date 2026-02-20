package com.qoobot.qooerp.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "system")
public class SystemProperties {

    /**
     * 字典配置
     */
    private Dict dict = new Dict();

    /**
     * 参数配置
     */
    private Config config = new Config();

    /**
     * 日志配置
     */
    private Log log = new Log();

    /**
     * 文件配置
     */
    private File file = new File();

    @Data
    public static class Dict {
        /**
         * 是否启用缓存
         */
        private boolean cacheEnabled = true;

        /**
         * 缓存过期时间（秒）
         */
        private int cacheTtl = 1800;
    }

    @Data
    public static class Config {
        /**
         * 是否启用缓存
         */
        private boolean cacheEnabled = true;
    }

    @Data
    public static class Log {
        /**
         * 日志保留天数
         */
        private int retentionDays = 90;

        /**
         * 是否异步记录
         */
        private boolean asyncEnabled = true;
    }

    @Data
    public static class File {
        /**
         * 文件上传路径
         */
        private String uploadPath = "/data/upload";

        /**
         * 最大文件大小
         */
        private String maxFileSize = "100MB";

        /**
         * 允许的文件扩展名
         */
        private String allowedExtensions = "jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,rar";
    }
}
