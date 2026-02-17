package com.qoobot.qooerp.notify.dto;

import com.qoobot.qooerp.notify.enums.NotifyStatusEnum;
import com.qoobot.qooerp.notify.enums.NotifyTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知记录DTO
 */
@Data
@Schema(description = "通知记录DTO")
public class NotifyRecordDTO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "通知编号")
    private String notifyNo;

    @Schema(description = "通知类型")
    private NotifyTypeEnum type;

    @Schema(description = "接收者")
    private String receiver;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "状态")
    private NotifyStatusEnum status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "重试时间")
    private LocalDateTime retryTime;

    @Schema(description = "发送时间")
    private LocalDateTime sentTime;

    @Schema(description = "扩展参数")
    private Object extraParams;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
