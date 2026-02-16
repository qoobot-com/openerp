package com.qoobot.common.exception;

import lombok.Getter;

/**
 * 权限异常
 */
@Getter
public class PermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    public PermissionException(String message) {
        super(message);
        this.code = 403;
        this.message = message;
    }

    public PermissionException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
        this.code = 403;
        this.message = message;
    }
}
