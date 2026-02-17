package com.qoobot.qooerp.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件上传配置
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:/data/qooerp/files}")
    private String uploadPath;

    @Value("${file.access.url:/file}")
    private String accessUrl;

    /**
     * 静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(accessUrl + "/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }
}
