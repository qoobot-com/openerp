package com.qoobot.qooerp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志VO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "操作日志VO")
public class SystemLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "模块名称")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "方法名称")
    private String method;

    @Schema(description = "请求参数")
    private String params;

    @Schema(description = "请求IP")
    private String ip;

    @Schema(description = "请求地点")
    private String location;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "状态：0-失败 1-成功")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "执行时间（毫秒）")
    private Integer costTime;

    @Schema(description = "操作时间")
    private LocalDateTime operateTime;
}
