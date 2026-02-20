package com.qoobot.qooerp.common.exception;

import lombok.Getter;

/**
 * 数据不存在异常
 */
@Getter
public class DataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 实体类
     */
    private final Class<?> entityClass;

    /**
     * 数据ID
     */
    private final Object id;

    /**
     * 错误消息
     */
    private final String message;

    public DataNotFoundException(String message) {
        super(message);
        this.entityClass = null;
        this.id = null;
        this.message = message;
    }

    public DataNotFoundException(Class<?> entityClass, Object id) {
        super(String.format("数据不存在: %s [id=%s]", entityClass.getSimpleName(), id));
        this.entityClass = entityClass;
        this.id = id;
        this.message = String.format("数据不存在: %s [id=%s]", entityClass.getSimpleName(), id);
    }

    public DataNotFoundException(Class<?> entityClass, String fieldName, Object value) {
        super(String.format("数据不存在: %s [%s=%s]", entityClass.getSimpleName(), fieldName, value));
        this.entityClass = entityClass;
        this.id = value;
        this.message = String.format("数据不存在: %s [%s=%s]", entityClass.getSimpleName(), fieldName, value);
    }
}
