package com.qoobot.qooerp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应码枚举
 * 包含HTTP状态码和业务错误码
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    // ========== 1xx 信息响应 ==========
    CONTINUE(100, "Continue", "继续"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols", "切换协议"),
    PROCESSING(102, "Processing", "处理中"),
    EARLY_HINTS(103, "Early Hints", "早期提示"),

    // ========== 2xx 成功响应 ==========
    SUCCESS(200, "OK", "操作成功"),
    CREATED(201, "Created", "创建成功"),
    ACCEPTED(202, "Accepted", "请求已接受"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information", "非权威信息"),
    NO_CONTENT(204, "No Content", "无内容"),
    RESET_CONTENT(205, "Reset Content", "重置内容"),
    PARTIAL_CONTENT(206, "Partial Content", "部分内容"),

    // ========== 3xx 重定向 ==========
    MULTIPLE_CHOICES(300, "Multiple Choices", "多种选择"),
    MOVED_PERMANENTLY(301, "Moved Permanently", "资源已永久移动"),
    FOUND(302, "Found", "资源临时移动"),
    SEE_OTHER(303, "See Other", "查看其他"),
    NOT_MODIFIED(304, "Not Modified", "资源未修改"),
    USE_PROXY(305, "Use Proxy", "使用代理"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect", "临时重定向"),
    PERMANENT_REDIRECT(308, "Permanent Redirect", "永久重定向"),

    // ========== 4xx 客户端错误 ==========
    BAD_REQUEST(400, "Bad Request", "请求参数错误"),
    UNAUTHORIZED(401, "Unauthorized", "未授权，请先登录"),
    PAYMENT_REQUIRED(402, "Payment Required", "需要付费"),
    FORBIDDEN(403, "Forbidden", "无权限访问"),
    NOT_FOUND(404, "Not Found", "资源不存在"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "请求方法不支持"),
    NOT_ACCEPTABLE(406, "Not Acceptable", "请求格式不可接受"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required", "代理认证失败"),
    REQUEST_TIMEOUT(408, "Request Timeout", "请求超时"),
    CONFLICT(409, "Conflict", "资源冲突"),
    GONE(410, "Gone", "资源已删除"),
    LENGTH_REQUIRED(411, "Length Required", "需要指定长度"),
    PRECONDITION_FAILED(412, "Precondition Failed", "前置条件失败"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large", "请求体过大"),
    URI_TOO_LONG(414, "URI Too Long", "URI过长"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type", "不支持的媒体类型"),
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable", "请求范围无效"),
    EXPECTATION_FAILED(417, "Expectation Failed", "期望失败"),
    IM_A_TEAPOT(418, "I'm a teapot", "我是一个茶壶"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity", "无法处理的实体"),
    TOO_MANY_REQUESTS(429, "Too Many Requests", "请求过于频繁"),

    // ========== 5xx 服务器错误 ==========
    INTERNAL_ERROR(500, "Internal Server Error", "系统内部错误"),
    NOT_IMPLEMENTED(501, "Not Implemented", "功能未实现"),
    BAD_GATEWAY(502, "Bad Gateway", "网关错误"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable", "服务不可用"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout", "网关超时"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported", "HTTP版本不支持"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates", "变体协商"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage", "存储空间不足"),

    // ========== 1xxxx 通用业务错误码 ==========
    BIZ_SUCCESS(10000, "SUCCESS", "操作成功"),
    BIZ_FAIL(10001, "FAIL", "操作失败"),
    UNKNOWN_ERROR(10002, "UNKNOWN_ERROR", "未知错误"),
    SYSTEM_ERROR(10003, "SYSTEM_ERROR", "系统异常"),
    NETWORK_ERROR(10004, "NETWORK_ERROR", "网络异常"),
    TIMEOUT_ERROR(10005, "TIMEOUT_ERROR", "请求超时"),

    // ========== 11xxx 参数验证错误码 ==========
    PARAM_ERROR(11000, "PARAM_ERROR", "参数错误"),
    PARAM_MISSING(11001, "PARAM_MISSING", "缺少必要参数"),
    PARAM_INVALID(11002, "PARAM_INVALID", "参数格式不正确"),
    PARAM_TYPE_ERROR(11003, "PARAM_TYPE_ERROR", "参数类型错误"),
    PARAM_OUT_OF_RANGE(11004, "PARAM_OUT_OF_RANGE", "参数超出范围"),

    // ========== 12xxx 用户相关错误码 ==========
    USER_NOT_FOUND(12000, "USER_NOT_FOUND", "用户不存在"),
    USER_ALREADY_EXISTS(12001, "USER_ALREADY_EXISTS", "用户已存在"),
    USER_DISABLED(12002, "USER_DISABLED", "用户已被禁用"),
    USER_LOCKED(12003, "USER_LOCKED", "用户已被锁定"),
    USER_EXPIRED(12004, "USER_EXPIRED", "用户已过期"),
    USERNAME_ALREADY_EXISTS(12005, "USERNAME_ALREADY_EXISTS", "用户名已存在"),
    EMAIL_ALREADY_EXISTS(12006, "EMAIL_ALREADY_EXISTS", "邮箱已存在"),
    PHONE_ALREADY_EXISTS(12007, "PHONE_ALREADY_EXISTS", "手机号已存在"),
    PASSWORD_ERROR(12008, "PASSWORD_ERROR", "密码错误"),
    PASSWORD_TOO_SIMPLE(12009, "PASSWORD_TOO_SIMPLE", "密码过于简单"),
    PASSWORD_EXPIRED(12010, "PASSWORD_EXPIRED", "密码已过期"),
    LOGIN_ERROR(12011, "LOGIN_ERROR", "登录失败"),
    LOGIN_PASSWORD_ERROR(12012, "LOGIN_PASSWORD_ERROR", "账号或密码错误"),
    LOGIN_ACCOUNT_LOCKED(12013, "LOGIN_ACCOUNT_LOCKED", "账号已被锁定"),
    LOGIN_ACCOUNT_DISABLED(12014, "LOGIN_ACCOUNT_DISABLED", "账号已被禁用"),
    LOGIN_ACCOUNT_EXPIRED(12015, "LOGIN_ACCOUNT_EXPIRED", "账号已过期"),

    // ========== 13xxx 权限相关错误码 ==========
    NO_PERMISSION(13000, "NO_PERMISSION", "无权限访问"),
    LOGIN_REQUIRED(13001, "LOGIN_REQUIRED", "请先登录"),
    TOKEN_INVALID(13002, "TOKEN_INVALID", "令牌无效"),
    TOKEN_EXPIRED(13003, "TOKEN_EXPIRED", "令牌已过期"),
    TOKEN_MISSING(13004, "TOKEN_MISSING", "令牌缺失"),
    ROLE_NOT_FOUND(13005, "ROLE_NOT_FOUND", "角色不存在"),
    PERMISSION_DENIED(13006, "PERMISSION_DENIED", "权限不足"),
    ROLE_ASSIGN_ERROR(13007, "ROLE_ASSIGN_ERROR", "角色分配失败"),
    PERMISSION_CONFIG_ERROR(13008, "PERMISSION_CONFIG_ERROR", "权限配置错误"),

    // ========== 14xxx 数据相关错误码 ==========
    DATA_NOT_FOUND(14000, "DATA_NOT_FOUND", "数据不存在"),
    DATA_ALREADY_EXISTS(14001, "DATA_ALREADY_EXISTS", "数据已存在"),
    DATA_DUPLICATE(14002, "DATA_DUPLICATE", "数据重复"),
    DATA_CONFLICT(14003, "DATA_CONFLICT", "数据冲突"),
    DATA_DELETED(14004, "DATA_DELETED", "数据已删除"),
    DATA_VERSION_ERROR(14005, "DATA_VERSION_ERROR", "数据版本错误"),
    DATA_REFERENCE_ERROR(14006, "DATA_REFERENCE_ERROR", "数据被引用，无法删除"),
    DATA_INTEGRITY_ERROR(14007, "DATA_INTEGRITY_ERROR", "数据完整性错误"),

    // ========== 15xxx 业务流程错误码 ==========
    PROCESS_NOT_FOUND(15000, "PROCESS_NOT_FOUND", "流程不存在"),
    PROCESS_INSTANCE_NOT_FOUND(15001, "PROCESS_INSTANCE_NOT_FOUND", "流程实例不存在"),
    TASK_NOT_FOUND(15002, "TASK_NOT_FOUND", "任务不存在"),
    TASK_ALREADY_COMPLETED(15003, "TASK_ALREADY_COMPLETED", "任务已完成"),
    TASK_ALREADY_CLAIMED(15004, "TASK_ALREADY_CLAIMED", "任务已被领取"),
    PROCESS_ERROR(15005, "PROCESS_ERROR", "流程执行错误"),
    TASK_CLAIM_FAILED(15006, "TASK_CLAIM_FAILED", "任务领取失败"),
    TASK_COMPLETE_FAILED(15007, "TASK_COMPLETE_FAILED", "任务完成失败"),

    // ========== 16xxx 审批相关错误码 ==========
    APPROVAL_NOT_FOUND(16000, "APPROVAL_NOT_FOUND", "审批记录不存在"),
    APPROVAL_ALREADY_APPROVED(16001, "APPROVAL_ALREADY_APPROVED", "审批已通过"),
    APPROVAL_ALREADY_REJECTED(16002, "APPROVAL_ALREADY_REJECTED", "审批已拒绝"),
    APPROVAL_ERROR(16003, "APPROVAL_ERROR", "审批失败"),
    APPROVAL_NODE_ERROR(16004, "APPROVAL_NODE_ERROR", "审批节点错误"),
    APPROVAL_FLOW_ERROR(16005, "APPROVAL_FLOW_ERROR", "审批流程错误"),

    // ========== 17xxx 文件相关错误码 ==========
    FILE_NOT_FOUND(17000, "FILE_NOT_FOUND", "文件不存在"),
    FILE_UPLOAD_ERROR(17001, "FILE_UPLOAD_ERROR", "文件上传失败"),
    FILE_DOWNLOAD_ERROR(17002, "FILE_DOWNLOAD_ERROR", "文件下载失败"),
    FILE_DELETE_ERROR(17003, "FILE_DELETE_ERROR", "文件删除失败"),
    FILE_TYPE_ERROR(17004, "FILE_TYPE_ERROR", "文件类型错误"),
    FILE_SIZE_EXCEED(17005, "FILE_SIZE_EXCEED", "文件大小超出限制"),
    FILE_NAME_ERROR(17006, "FILE_NAME_ERROR", "文件名错误"),
    FILE_PATH_ERROR(17007, "FILE_PATH_ERROR", "文件路径错误"),

    // ========== 18xxx 导入导出错误码 ==========
    IMPORT_ERROR(18000, "IMPORT_ERROR", "导入失败"),
    EXPORT_ERROR(18001, "EXPORT_ERROR", "导出失败"),
    IMPORT_TEMPLATE_ERROR(18002, "IMPORT_TEMPLATE_ERROR", "导入模板错误"),
    DATA_FORMAT_ERROR(18003, "DATA_FORMAT_ERROR", "数据格式错误"),
    DATA_PARSE_ERROR(18004, "DATA_PARSE_ERROR", "数据解析错误"),

    // ========== 19xxx 缓存相关错误码 ==========
    CACHE_ERROR(19000, "CACHE_ERROR", "缓存操作失败"),
    CACHE_KEY_NOT_FOUND(19001, "CACHE_KEY_NOT_FOUND", "缓存键不存在"),
    CACHE_CONNECTION_ERROR(19002, "CACHE_CONNECTION_ERROR", "缓存连接错误"),
    CACHE_TIMEOUT(19003, "CACHE_TIMEOUT", "缓存操作超时"),

    // ========== 20xxx 数据库相关错误码 ==========
    DATABASE_ERROR(20000, "DATABASE_ERROR", "数据库操作失败"),
    DB_CONNECTION_ERROR(20001, "DB_CONNECTION_ERROR", "数据库连接失败"),
    DB_TIMEOUT(20002, "DB_TIMEOUT", "数据库操作超时"),
    DB_LOCK_ERROR(20003, "DB_LOCK_ERROR", "数据库锁冲突"),
    DB_CONSTRAINT_ERROR(20004, "DB_CONSTRAINT_ERROR", "数据库约束错误"),
    DB_TRANSACTION_ERROR(20005, "DB_TRANSACTION_ERROR", "数据库事务错误"),

    // ========== 21xxx 短信邮件错误码 ==========
    SMS_SEND_ERROR(21000, "SMS_SEND_ERROR", "短信发送失败"),
    SMS_TEMPLATE_ERROR(21001, "SMS_TEMPLATE_ERROR", "短信模板错误"),
    SMS_LIMIT_EXCEED(21002, "SMS_LIMIT_EXCEED", "短信发送次数超限"),
    EMAIL_SEND_ERROR(21003, "EMAIL_SEND_ERROR", "邮件发送失败"),
    EMAIL_TEMPLATE_ERROR(21004, "EMAIL_TEMPLATE_ERROR", "邮件模板错误"),

    // ========== 22xxx 支付相关错误码 ==========
    PAYMENT_ERROR(22000, "PAYMENT_ERROR", "支付失败"),
    PAYMENT_NOT_FOUND(22001, "PAYMENT_NOT_FOUND", "支付记录不存在"),
    PAYMENT_EXPIRED(22002, "PAYMENT_EXPIRED", "支付已过期"),
    PAYMENT_CANCELLED(22003, "PAYMENT_CANCELLED", "支付已取消"),
    PAYMENT_REFUND_ERROR(22004, "PAYMENT_REFUND_ERROR", "退款失败"),
    PAYMENT_AMOUNT_ERROR(22005, "PAYMENT_AMOUNT_ERROR", "金额错误"),
    PAYMENT_CHANNEL_ERROR(22006, "PAYMENT_CHANNEL_ERROR", "支付渠道错误"),

    // ========== 23xxx 库存相关错误码 ==========
    STOCK_NOT_ENOUGH(23000, "STOCK_NOT_ENOUGH", "库存不足"),
    STOCK_ERROR(23001, "STOCK_ERROR", "库存操作失败"),
    STOCK_LOCK_ERROR(23002, "STOCK_LOCK_ERROR", "库存锁定失败"),
    STOCK_UNLOCK_ERROR(23003, "STOCK_UNLOCK_ERROR", "库存解锁失败"),

    // ========== 24xxx 订单相关错误码 ==========
    ORDER_NOT_FOUND(24000, "ORDER_NOT_FOUND", "订单不存在"),
    ORDER_CREATE_ERROR(24001, "ORDER_CREATE_ERROR", "订单创建失败"),
    ORDER_UPDATE_ERROR(24002, "ORDER_UPDATE_ERROR", "订单更新失败"),
    ORDER_CANCEL_ERROR(24003, "ORDER_CANCEL_ERROR", "订单取消失败"),
    ORDER_STATUS_ERROR(24004, "ORDER_STATUS_ERROR", "订单状态错误"),
    ORDER_PAY_ERROR(24005, "ORDER_PAY_ERROR", "订单支付失败"),
    ORDER_DELIVERY_ERROR(24006, "ORDER_DELIVERY_ERROR", "订单发货失败"),

    // ========== 25xxx 组织架构相关错误码 ==========
    DEPT_NOT_FOUND(25000, "DEPT_NOT_FOUND", "部门不存在"),
    DEPT_ALREADY_EXISTS(25001, "DEPT_ALREADY_EXISTS", "部门已存在"),
    DEPT_HAS_USERS(25002, "DEPT_HAS_USERS", "部门下有用户，无法删除"),
    DEPT_HAS_CHILDREN(25003, "DEPT_HAS_CHILDREN", "部门下有子部门，无法删除"),
    POSITION_NOT_FOUND(25004, "POSITION_NOT_FOUND", "岗位不存在"),
    POSITION_ALREADY_EXISTS(25005, "POSITION_ALREADY_EXISTS", "岗位已存在"),

    // ========== 26xxx 薪资相关错误码 ==========
    SALARY_ERROR(26000, "SALARY_ERROR", "薪资计算失败"),
    SALARY_PERIOD_ERROR(26001, "SALARY_PERIOD_ERROR", "薪资周期错误"),
    SALARY_RULE_ERROR(26002, "SALARY_RULE_ERROR", "薪资规则错误"),
    SALARY_APPROVAL_ERROR(26003, "SALARY_APPROVAL_ERROR", "薪资审批失败"),
    SALARY_PAY_ERROR(26004, "SALARY_PAY_ERROR", "薪资发放失败"),

    // ========== 27xxx 考勤相关错误码 ==========
    ATTENDANCE_ERROR(27000, "ATTENDANCE_ERROR", "考勤操作失败"),
    ATTENDANCE_RECORD_ERROR(27001, "ATTENDANCE_RECORD_ERROR", "考勤记录错误"),
    ATTENDANCE_RULE_ERROR(27002, "ATTENDANCE_RULE_ERROR", "考勤规则错误"),
    ATTENDANCE_LEAVE_ERROR(27003, "ATTENDANCE_LEAVE_ERROR", "请假操作失败"),
    ATTENDANCE_OVERTIME_ERROR(27004, "ATTENDANCE_OVERTIME_ERROR", "加班操作失败"),

    // ========== 28xxx 绩效相关错误码 ==========
    PERFORMANCE_ERROR(28000, "PERFORMANCE_ERROR", "绩效操作失败"),
    PERFORMANCE_PERIOD_ERROR(28001, "PERFORMANCE_PERIOD_ERROR", "绩效周期错误"),
    PERFORMANCE_GOAL_ERROR(28002, "PERFORMANCE_GOAL_ERROR", "绩效目标错误"),
    PERFORMANCE_ASSESSMENT_ERROR(28003, "PERFORMANCE_ASSESSMENT_ERROR", "绩效考核失败"),

    // ========== 29xxx 合同相关错误码 ==========
    CONTRACT_NOT_FOUND(29000, "CONTRACT_NOT_FOUND", "合同不存在"),
    CONTRACT_ALREADY_EXISTS(29001, "CONTRACT_ALREADY_EXISTS", "合同已存在"),
    CONTRACT_EXPIRED(29002, "CONTRACT_EXPIRED", "合同已过期"),
    CONTRACT_TERMINATED(29003, "CONTRACT_TERMINATED", "合同已终止"),
    CONTRACT_RENEW_ERROR(29004, "CONTRACT_RENEW_ERROR", "合同续签失败"),

    // ========== 30xxx 招聘相关错误码 ==========
    RECRUITMENT_ERROR(30000, "RECRUITMENT_ERROR", "招聘操作失败"),
    RESUME_NOT_FOUND(30001, "RESUME_NOT_FOUND", "简历不存在"),
    INTERVIEW_NOT_FOUND(30002, "INTERVIEW_NOT_FOUND", "面试记录不存在"),
    INTERVIEW_ERROR(30003, "INTERVIEW_ERROR", "面试安排失败"),
    OFFER_ERROR(30004, "OFFER_ERROR", "Offer发放失败"),

    // ========== 31xxx 培训相关错误码 ==========
    TRAINING_ERROR(31000, "TRAINING_ERROR", "培训操作失败"),
    TRAINING_COURSE_NOT_FOUND(31001, "TRAINING_COURSE_NOT_FOUND", "培训课程不存在"),
    TRAINING_ENROLL_ERROR(31002, "TRAINING_ENROLL_ERROR", "培训报名失败"),
    TRAINING_COMPLETE_ERROR(31003, "TRAINING_COMPLETE_ERROR", "培训完成失败"),

    // ========== 32xxx 资产相关错误码 ==========
    ASSET_NOT_FOUND(32000, "ASSET_NOT_FOUND", "资产不存在"),
    ASSET_ALREADY_EXISTS(32001, "ASSET_ALREADY_EXISTS", "资产已存在"),
    ASSET_BORROW_ERROR(32002, "ASSET_BORROW_ERROR", "资产借用失败"),
    ASSET_RETURN_ERROR(32003, "ASSET_RETURN_ERROR", "资产归还失败"),
    ASSET_MAINTENANCE_ERROR(32004, "ASSET_MAINTENANCE_ERROR", "资产维护失败"),

    // ========== 33xxx 系统配置相关错误码 ==========
    CONFIG_NOT_FOUND(33000, "CONFIG_NOT_FOUND", "配置不存在"),
    CONFIG_SAVE_ERROR(33001, "CONFIG_SAVE_ERROR", "配置保存失败"),
    DICT_NOT_FOUND(33002, "DICT_NOT_FOUND", "字典不存在"),
    DICT_TYPE_ERROR(33003, "DICT_TYPE_ERROR", "字典类型错误"),
    DICT_ITEM_ERROR(33004, "DICT_ITEM_ERROR", "字典项错误"),

    // ========== 34xxx 日志相关错误码 ==========
    LOG_ERROR(34000, "LOG_ERROR", "日志记录失败"),
    LOG_QUERY_ERROR(34001, "LOG_QUERY_ERROR", "日志查询失败"),
    LOG_EXPORT_ERROR(34002, "LOG_EXPORT_ERROR", "日志导出失败"),

    // ========== 35xxx 消息通知相关错误码 ==========
    MESSAGE_NOT_FOUND(35000, "MESSAGE_NOT_FOUND", "消息不存在"),
    MESSAGE_SEND_ERROR(35001, "MESSAGE_SEND_ERROR", "消息发送失败"),
    MESSAGE_READ_ERROR(35002, "MESSAGE_READ_ERROR", "消息标记已读失败"),
    MESSAGE_DELETE_ERROR(35003, "MESSAGE_DELETE_ERROR", "消息删除失败"),
    NOTIFICATION_ERROR(35004, "NOTIFICATION_ERROR", "通知发送失败"),

    // ========== 36xxx 调度任务相关错误码 ==========
    SCHEDULE_TASK_NOT_FOUND(36000, "SCHEDULE_TASK_NOT_FOUND", "任务不存在"),
    SCHEDULE_TASK_ERROR(36001, "SCHEDULE_TASK_ERROR", "任务执行失败"),
    SCHEDULE_TASK_TIMEOUT(36002, "SCHEDULE_TASK_TIMEOUT", "任务执行超时"),

    // ========== 37xxx 外部接口相关错误码 ==========
    EXTERNAL_API_ERROR(37000, "EXTERNAL_API_ERROR", "外部接口调用失败"),
    EXTERNAL_API_TIMEOUT(37001, "EXTERNAL_API_TIMEOUT", "外部接口超时"),
    EXTERNAL_API_AUTH_ERROR(37002, "EXTERNAL_API_AUTH_ERROR", "外部接口认证失败"),
    EXTERNAL_API_PARAM_ERROR(37003, "EXTERNAL_API_PARAM_ERROR", "外部接口参数错误"),

    // ========== 38xxx 防重复提交错误码 ==========
    DUPLICATE_SUBMIT(38000, "DUPLICATE_SUBMIT", "请勿重复提交"),
    OPERATION_TOO_FAST(38001, "OPERATION_TOO_FAST", "操作过于频繁");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 英文标识
     */
    private final String key;

    /**
     * 中文描述
     */
    private final String message;

    /**
     * 根据响应码获取枚举
     */
    public static ResponseCode fromCode(Integer code) {
        if (code == null) {
            return INTERNAL_ERROR;
        }
        for (ResponseCode responseCode : values()) {
            if (responseCode.getCode().equals(code)) {
                return responseCode;
            }
        }
        return INTERNAL_ERROR;
    }

    /**
     * 判断是否为成功状态
     */
    public boolean isSuccess() {
        return code == 200 || code == 10000;
    }

    /**
     * 判断是否为HTTP 2xx成功状态
     */
    public boolean isHttpSuccess() {
        return code >= 200 && code < 300;
    }

    /**
     * 判断是否为客户端错误
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }

    /**
     * 判断是否为服务器错误
     */
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }

    /**
     * 判断是否为业务错误
     */
    public boolean isBusinessError() {
        return code >= 10000;
    }

    /**
     * 获取HTTP状态码
     */
    public int getHttpStatusCode() {
        if (isBusinessError()) {
            return 200; // 业务错误统一返回200，通过响应码区分
        }
        return code;
    }
}
