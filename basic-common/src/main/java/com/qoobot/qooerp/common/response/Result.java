package com.qoobot.qooerp.common.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.qoobot.qooerp.common.enums.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 追踪ID
     */
    private String traceId;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public Result(ResponseCode responseCode, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public Result(ResponseCode responseCode, String customMessage, T data) {
        this.code = responseCode.getCode();
        this.message = customMessage;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(ResponseCode.SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResponseCode.SUCCESS, data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResponseCode.SUCCESS, message, data);
    }

    public static <T> Result<T> success(ResponseCode responseCode) {
        return new Result<>(responseCode, null);
    }

    public static <T> Result<T> success(ResponseCode responseCode, T data) {
        return new Result<>(responseCode, data);
    }

    public static <T> Result<T> success(ResponseCode responseCode, String customMessage, T data) {
        return new Result<>(responseCode, customMessage, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error() {
        return new Result<>(ResponseCode.SYSTEM_ERROR, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResponseCode.SYSTEM_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(ResponseCode responseCode) {
        return new Result<>(responseCode, null);
    }

    public static <T> Result<T> error(ResponseCode responseCode, String customMessage) {
        return new Result<>(responseCode.getCode(), customMessage, null);
    }

    public static <T> Result<T> fail() {
        return new Result<>(ResponseCode.SYSTEM_ERROR, null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(ResponseCode.SYSTEM_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(ResponseCode responseCode) {
        return new Result<>(responseCode, null);
    }

    public static <T> Result<T> fail(ResponseCode responseCode, String customMessage) {
        return new Result<>(responseCode.getCode(), customMessage, null);
    }

    /**
     * 根据响应码构建结果
     */
    public static <T> Result<T> from(ResponseCode responseCode) {
        return new Result<>(responseCode, null);
    }

    public static <T> Result<T> from(ResponseCode responseCode, T data) {
        return new Result<>(responseCode, data);
    }

    public static <T> Result<T> from(ResponseCode responseCode, String customMessage, T data) {
        return new Result<>(responseCode, customMessage, data);
    }

    /**
     * 自定义响应
     */
    public static <T> Result<T> of(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> of(ResponseCode responseCode) {
        return new Result<>(responseCode, null);
    }

    public static <T> Result<T> of(ResponseCode responseCode, T data) {
        return new Result<>(responseCode, data);
    }

    public static <T> Result<T> of(ResponseCode responseCode, String customMessage, T data) {
        return new Result<>(responseCode, customMessage, data);
    }

    /**
     * 分页响应
     */
    public static <T> PageResult<T> page(Page<T> page) {
        return PageResult.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return ResponseCode.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 设置追踪ID
     */
    public Result<T> setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
