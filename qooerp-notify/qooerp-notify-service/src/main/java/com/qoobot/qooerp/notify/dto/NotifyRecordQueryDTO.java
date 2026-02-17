package com.qoobot.qooerp.notify.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.notify.enums.NotifyStatusEnum;
import com.qoobot.qooerp.notify.enums.NotifyTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知记录查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通知记录查询DTO")
public class NotifyRecordQueryDTO extends Page<NotifyRecordQueryDTO> {

    @Schema(description = "通知编号")
    private String notifyNo;

    @Schema(description = "通知类型")
    private NotifyTypeEnum type;

    @Schema(description = "接收者")
    private String receiver;

    @Schema(description = "状态")
    private NotifyStatusEnum status;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
