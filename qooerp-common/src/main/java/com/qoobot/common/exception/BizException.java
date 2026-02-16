package com.qoobot.common.exception;

import com.qoobot.common.enums.ResponseCode;
import com.qoobot.common.response.Result;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    public BizException(String message) {
        super(message);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public BizException(ResponseCode responseCode, String customMessage) {
        super(customMessage);
        this.code = responseCode.getCode();
        this.message = customMessage;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public BizException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    /**
     * 转换为Result
     */
    public Result<Void> toResult() {
        return Result.error(this.code, this.message);
    }
}
