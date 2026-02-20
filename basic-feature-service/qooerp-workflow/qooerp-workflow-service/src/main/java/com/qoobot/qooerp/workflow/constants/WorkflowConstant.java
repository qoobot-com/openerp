package com.qoobot.qooerp.workflow.constants;

/**
 * 工作流常量
 */
public class WorkflowConstant {

    /**
     * 流程状态
     */
    public static final class ProcessStatus {
        public static final String RUNNING = "running";
        public static final String COMPLETED = "completed";
        public static final String SUSPENDED = "suspended";
        public static final String TERMINATED = "terminated";
    }

    /**
     * 任务状态
     */
    public static final class TaskStatus {
        public static final String PENDING = "pending";
        public static final String COMPLETED = "completed";
        public static final String DELEGATED = "delegated";
        public static final String SUSPENDED = "suspended";
    }

    /**
     * 通知类型
     */
    public static final class NotificationType {
        public static final String TASK_ASSIGNED = "task_assigned";
        public static final String TASK_COMPLETED = "task_completed";
        public static final String TASK_TIMEOUT = "task_timeout";
        public static final String PROCESS_COMPLETED = "process_completed";
        public static final String TASK_CC = "task_cc";
    }

    /**
     * 通知方式
     */
    public static final class NotifyMethod {
        public static final String SYSTEM = "system";
        public static final String EMAIL = "email";
        public static final String SMS = "sms";
        public static final String WECHAT = "wechat";
        public static final String DINGTALK = "dingtalk";
    }

    /**
     * 表单类型
     */
    public static final class FormType {
        public static final String SINGLE = "single";
        public static final String MASTER_DETAIL = "master-detail";
    }

    /**
     * 表单状态
     */
    public static final class FormStatus {
        public static final Integer DRAFT = 0;
        public static final Integer PUBLISHED = 1;
        public static final Integer UNPUBLISHED = 2;
    }

    /**
     * 字段类型
     */
    public static final class FieldType {
        public static final String TEXT = "text";
        public static final String NUMBER = "number";
        public static final String DATE = "date";
        public static final String DATETIME = "datetime";
        public static final String SELECT = "select";
        public static final String CHECKBOX = "checkbox";
        public static final String RADIO = "radio";
        public static final String TEXTAREA = "textarea";
        public static final String UPLOAD = "upload";
        public static final String USER_SELECT = "user_select";
        public static final String DEPT_SELECT = "dept_select";
    }

    /**
     * 缓存名称
     */
    public static final class CacheName {
        public static final String PROCESS_DEFINITION = "process_definition";
        public static final String PROCESS_INSTANCE = "process_instance";
        public static final String TASK = "task";
        public static final String FORM = "form";
        public static final String CATEGORY = "category";
    }

    /**
     * 流程变量键
     */
    public static final class VariableKey {
        public static final String START_USER_ID = "startUserId";
        public static final String START_USER_NAME = "startUserName";
        public static final String START_DEPT_ID = "startDeptId";
        public static final String START_DEPT_NAME = "startDeptName";
        public static final String BUSINESS_KEY = "businessKey";
        public static final String FORM_DATA = "formData";
        public static final String APPROVAL_RESULT = "approvalResult";
        public static final String APPROVAL_COMMENT = "approvalComment";
    }
}
