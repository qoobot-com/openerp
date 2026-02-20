package com.qoobot.qooerp.common.exception;

import com.qoobot.qooerp.common.enums.ResponseCode;
import com.qoobot.qooerp.common.response.Result;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public BusinessException(ResponseCode responseCode, String customMessage) {
        super(customMessage);
        this.code = responseCode.getCode();
        this.message = customMessage;
    }

    public BusinessException(com.qoobot.qooerp.common.constant.ErrorCodeConstant errorCodeConstant) {
        super(errorCodeConstant.getMessage());
        this.code = errorCodeConstant.getCode();
        this.message = errorCodeConstant.getMessage();
    }

    public BusinessException(com.qoobot.qooerp.common.constant.ErrorCodeConstant errorCodeConstant, String customMessage) {
        super(customMessage);
        this.code = errorCodeConstant.getCode();
        this.message = customMessage;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public BusinessException(ResponseCode responseCode, Throwable cause) {
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
