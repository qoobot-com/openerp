package com.qoobot.common.exception;

import com.qoobot.common.enums.ResponseCode;
import lombok.Getter;

/**
 * 数据库异常
 */
@Getter
public class DbException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    public DbException(String message) {
        super(message);
        this.code = ResponseCode.DATABASE_ERROR.getCode();
        this.message = message;
    }

    public DbException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public DbException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.DATABASE_ERROR.getCode();
        this.message = message;
    }

    public DbException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }
}
