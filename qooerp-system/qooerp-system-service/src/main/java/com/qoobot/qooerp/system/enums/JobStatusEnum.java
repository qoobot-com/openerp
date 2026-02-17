package com.qoobot.qooerp.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态枚举
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum JobStatusEnum {

    /**
     * 暂停
     */
    PAUSED(0, "暂停"),

    /**
     * 运行中
     */
    RUNNING(1, "运行中");

    /**
     * 状态值
     */
    private final Integer value;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     */
    public static JobStatusEnum fromValue(Integer value) {
        for (JobStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid job status value: " + value);
    }
}
