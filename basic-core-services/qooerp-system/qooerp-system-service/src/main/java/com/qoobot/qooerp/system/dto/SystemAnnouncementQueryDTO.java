package com.qoobot.qooerp.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统公告查询DTO
 */
@Data
public class SystemAnnouncementQueryDTO {

    /**
     * 公告标题（模糊查询）
     */
    private String title;

    /**
     * 公告类型
     */
    private String announcementType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
