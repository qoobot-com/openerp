package com.qoobot.qooerp.common.exception;

import com.qoobot.qooerp.common.enums.ResponseCode;
import lombok.Getter;

/**
 * 系统异常
 */
@Getter
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    public SystemException(String message) {
        super(message);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public SystemException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public SystemException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public SystemException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }
}
