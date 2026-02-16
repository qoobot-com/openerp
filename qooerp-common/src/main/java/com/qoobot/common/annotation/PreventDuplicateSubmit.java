package com.qoobot.common.annotation;

import java.lang.annotation.*;

/**
 * 防重复提交注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreventDuplicateSubmit {

    /**
     * 超时时间(毫秒)
     */
    long timeout() default 3000;

    /**
     * 提示消息
     */
    String message() default "请勿重复提交";
}
