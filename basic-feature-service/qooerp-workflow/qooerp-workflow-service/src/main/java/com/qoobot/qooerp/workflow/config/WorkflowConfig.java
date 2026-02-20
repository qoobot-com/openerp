package com.qoobot.qooerp.workflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 工作流配置
 */
@Configuration
@ConfigurationProperties(prefix = "workflow")
public class WorkflowConfig {

    /**
     * 流程图配置
     */
    private Diagram diagram = new Diagram();

    /**
     * 缓存配置
     */
    private Cache cache = new Cache();

    /**
     * 超时配置
     */
    private Timeout timeout = new Timeout();

    /**
     * 通知配置
     */
    private Notification notification = new Notification();

    /**
     * 表单配置
     */
    private Form form = new Form();

    public static class Diagram {
        private boolean enabled = true;
        private String fontName = "宋体";
        private int fontSize = 14;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getFontName() { return fontName; }
        public void setFontName(String fontName) { this.fontName = fontName; }
        public int getFontSize() { return fontSize; }
        public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    }

    public static class Cache {
        private boolean enabled = true;
        private int definitionCacheSize = 100;
        private int processCacheSize = 500;
        private int taskCacheSize = 1000;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getDefinitionCacheSize() { return definitionCacheSize; }
        public void setDefinitionCacheSize(int definitionCacheSize) { this.definitionCacheSize = definitionCacheSize; }
        public int getProcessCacheSize() { return processCacheSize; }
        public void setProcessCacheSize(int processCacheSize) { this.processCacheSize = processCacheSize; }
        public int getTaskCacheSize() { return taskCacheSize; }
        public void setTaskCacheSize(int taskCacheSize) { this.taskCacheSize = taskCacheSize; }
    }

    public static class Timeout {
        private long defaultTaskDuration = 86400;
        private long defaultProcessDuration = 604800;
        private long checkInterval = 300;

        public long getDefaultTaskDuration() { return defaultTaskDuration; }
        public void setDefaultTaskDuration(long defaultTaskDuration) { this.defaultTaskDuration = defaultTaskDuration; }
        public long getDefaultProcessDuration() { return defaultProcessDuration; }
        public void setDefaultProcessDuration(long defaultProcessDuration) { this.defaultProcessDuration = defaultProcessDuration; }
        public long getCheckInterval() { return checkInterval; }
        public void setCheckInterval(long checkInterval) { this.checkInterval = checkInterval; }
    }

    public static class Notification {
        private boolean enabled = true;
        private boolean taskNotify = true;
        private boolean approvalNotify = true;
        private boolean timeoutNotify = true;
        private boolean completeNotify = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isTaskNotify() { return taskNotify; }
        public void setTaskNotify(boolean taskNotify) { this.taskNotify = taskNotify; }
        public boolean isApprovalNotify() { return approvalNotify; }
        public void setApprovalNotify(boolean approvalNotify) { this.approvalNotify = approvalNotify; }
        public boolean isTimeoutNotify() { return timeoutNotify; }
        public void setTimeoutNotify(boolean timeoutNotify) { this.timeoutNotify = timeoutNotify; }
        public boolean isCompleteNotify() { return completeNotify; }
        public void setCompleteNotify(boolean completeNotify) { this.completeNotify = completeNotify; }
    }

    public static class Form {
        private boolean enabled = true;
        private long maxFormSize = 10485760;
        private String allowedFormats = "json,xml";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public long getMaxFormSize() { return maxFormSize; }
        public void setMaxFormSize(long maxFormSize) { this.maxFormSize = maxFormSize; }
        public String getAllowedFormats() { return allowedFormats; }
        public void setAllowedFormats(String allowedFormats) { this.allowedFormats = allowedFormats; }
    }

    public Diagram getDiagram() { return diagram; }
    public void setDiagram(Diagram diagram) { this.diagram = diagram; }
    public Cache getCache() { return cache; }
    public void setCache(Cache cache) { this.cache = cache; }
    public Timeout getTimeout() { return timeout; }
    public void setTimeout(Timeout timeout) { this.timeout = timeout; }
    public Notification getNotification() { return notification; }
    public void setNotification(Notification notification) { this.notification = notification; }
    public Form getForm() { return form; }
    public void setForm(Form form) { this.form = form; }
}
