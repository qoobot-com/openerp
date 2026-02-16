package com.qoobot.common.exception;

import com.qoobot.common.enums.ResponseCode;
import lombok.Getter;

/**
 * 缓存异常
 */
@Getter
public class CacheException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    public CacheException(String message) {
        super(message);
        this.code = ResponseCode.CACHE_ERROR.getCode();
        this.message = message;
    }

    public CacheException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.CACHE_ERROR.getCode();
        this.message = message;
    }

    public CacheException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }
}
