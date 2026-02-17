package com.qoobot.qooerp.system.exception;

import lombok.Getter;

/**
 * 系统模块业务异常
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Getter
public class SystemException extends RuntimeException {

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 错误信息
     */
    private final String errorMessage;

    public SystemException(String errorMessage) {
        super(errorMessage);
        this.errorCode = "SYSTEM_ERROR";
        this.errorMessage = errorMessage;
    }

    public SystemException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public SystemException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
