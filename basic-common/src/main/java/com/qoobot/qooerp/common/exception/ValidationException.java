package com.qoobot.qooerp.common.exception;

import lombok.Getter;

/**
 * 参数校验异常
 */
@Getter
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名
     */
    private final String fieldName;

    /**
     * 错误消息
     */
    private final String message;

    public ValidationException(String message) {
        super(message);
        this.fieldName = null;
        this.message = message;
    }

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
        this.message = message;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = null;
        this.message = message;
    }
}
