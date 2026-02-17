package com.qoobot.qooerp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统参数VO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "系统参数VO")
public class SystemConfigVO {

    @Schema(description = "参数ID")
    private Long id;

    @Schema(description = "参数名称")
    private String configName;

    @Schema(description = "参数键")
    private String configKey;

    @Schema(description = "参数值")
    private String configValue;

    @Schema(description = "参数类型")
    private String configType;

    @Schema(description = "是否系统参数：0-否 1-是")
    private Integer isSystem;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updateBy;
}
