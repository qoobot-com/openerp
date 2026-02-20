package com.qoobot.qooerp.notify.dto;

import com.qoobot.qooerp.notify.enums.NotifyTypeEnum;
import com.qoobot.qooerp.notify.enums.NotifyStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统计查询DTO
 */
@Data
@Schema(description = "统计查询DTO")
public class StatisticsDTO {

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "通知类型")
    private NotifyTypeEnum type;

    @Schema(description = "通知状态")
    private NotifyStatusEnum status;
}
