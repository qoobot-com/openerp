package com.qoobot.qooerp.organization.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 组织架构配置
 */
@Configuration
@ConfigurationProperties(prefix = "organization")
public class OrganizationConfig {

    /**
     * 最大公司层级
     */
    private Integer maxCompanyLevel = 10;

    /**
     * 最大部门层级
     */
    private Integer maxDeptLevel = 20;

    /**
     * 启用部门缓存
     */
    private Boolean enableDeptCache = true;

    public Integer getMaxCompanyLevel() {
        return maxCompanyLevel;
    }

    public void setMaxCompanyLevel(Integer maxCompanyLevel) {
        this.maxCompanyLevel = maxCompanyLevel;
    }

    public Integer getMaxDeptLevel() {
        return maxDeptLevel;
    }

    public void setMaxDeptLevel(Integer maxDeptLevel) {
        this.maxDeptLevel = maxDeptLevel;
    }

    public Boolean getEnableDeptCache() {
        return enableDeptCache;
    }

    public void setEnableDeptCache(Boolean enableDeptCache) {
        this.enableDeptCache = enableDeptCache;
    }
}
