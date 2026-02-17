package com.qoobot.qooerp.auth.exception;

import lombok.Getter;

/**
 * 认证异常
 */
@Getter
public class AuthException extends RuntimeException {

    private final String code;

    public AuthException(String code, String message) {
        super(message);
        this.code = code;
    }

    public AuthException(String message) {
        super(message);
        this.code = "AUTH_ERROR";
    }
}
