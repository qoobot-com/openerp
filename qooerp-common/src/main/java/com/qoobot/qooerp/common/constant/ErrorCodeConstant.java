package com.qoobot.qooerp.common.constant;

import lombok.Getter;

/**
 * 错误码常量
 */
@Getter
public enum ErrorCodeConstant {

    // ========== 通用错误码 ==========
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "系统内部错误"),

    // ========== 业务错误码 ==========
    BUSINESS_ERROR(1000, "业务异常"),
    DATA_NOT_FOUND(1001, "数据不存在"),
    DATA_EXIST(1002, "数据已存在"),
    DATA_CONFLICT(1003, "数据冲突"),
    OPERATION_FAILED(1004, "操作失败"),

    // ========== 验证错误码 ==========
    VALIDATION_ERROR(2000, "参数校验失败"),
    PARAM_REQUIRED(2001, "必填参数不能为空"),
    PARAM_INVALID(2002, "参数格式不正确"),
    PARAM_LENGTH_ERROR(2003, "参数长度错误"),
    PARAM_RANGE_ERROR(2004, "参数范围错误"),

    // ========== 权限错误码 ==========
    PERMISSION_ERROR(3000, "权限异常"),
    NO_PERMISSION(3001, "无权限访问"),
    TOKEN_INVALID(3002, "令牌无效"),
    TOKEN_EXPIRED(3003, "令牌已过期"),
    ACCOUNT_DISABLED(3004, "账号已被禁用"),
    ACCOUNT_LOCKED(3005, "账号已被锁定"),

    // ========== 用户错误码 ==========
    USER_NOT_EXIST(4000, "用户不存在"),
    USER_ACCOUNT_EXIST(4001, "账号已存在"),
    USER_PASSWORD_ERROR(4002, "密码错误"),
    USER_PASSWORD_NOT_MATCH(4003, "两次密码输入不一致"),
    USER_LOGIN_SUCCESS(4004, "登录成功"),
    USER_LOGIN_FAILED(4005, "登录失败"),

    // ========== 缓存错误码 ==========
    CACHE_ERROR(5000, "缓存异常"),
    CACHE_KEY_NOT_EXIST(5001, "缓存键不存在"),
    CACHE_SET_FAILED(5002, "设置缓存失败"),
    CACHE_GET_FAILED(5003, "获取缓存失败"),

    // ========== 数据库错误码 ==========
    DB_ERROR(6000, "数据库异常"),
    DB_INSERT_FAILED(6001, "插入数据失败"),
    DB_UPDATE_FAILED(6002, "更新数据失败"),
    DB_DELETE_FAILED(6003, "删除数据失败"),
    DB_QUERY_FAILED(6004, "查询数据失败"),

    // ========== 文件错误码 ==========
    FILE_ERROR(7000, "文件异常"),
    FILE_UPLOAD_FAILED(7001, "文件上传失败"),
    FILE_DOWNLOAD_FAILED(7002, "文件下载失败"),
    FILE_NOT_EXIST(7003, "文件不存在"),
    FILE_SIZE_EXCEED(7004, "文件大小超出限制"),
    FILE_TYPE_NOT_SUPPORT(7005, "不支持的文件类型"),

    // ========== 日期时间错误码 ==========
    DATE_ERROR(8000, "日期时间异常"),
    DATE_FORMAT_ERROR(8001, "日期格式错误"),
    DATE_PARSE_ERROR(8002, "日期解析错误"),
    DATE_RANGE_ERROR(8003, "日期范围错误"),

    // ========== 接口限流错误码 ==========
    RATE_LIMIT_ERROR(9000, "接口限流"),
    TOO_MANY_REQUESTS(9001, "请求过于频繁"),

    // ========== 分布式锁错误码 ==========
    LOCK_ERROR(10000, "分布式锁异常"),
    LOCK_ACQUIRE_FAILED(10001, "获取锁失败"),
    LOCK_RELEASE_FAILED(10002, "释放锁失败"),
    LOCK_TIMEOUT(10003, "锁等待超时");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCodeConstant(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取枚举
     */
    public static ErrorCodeConstant fromCode(Integer code) {
        for (ErrorCodeConstant error : values()) {
            if (error.getCode().equals(code)) {
                return error;
            }
        }
        return null;
    }

    /**
     * 判断是否成功
     */
    public static boolean isSuccess(Integer code) {
        return SUCCESS.getCode().equals(code);
    }

    /**
     * 判断是否错误
     */
    public static boolean isError(Integer code) {
        return !isSuccess(code);
    }
}
