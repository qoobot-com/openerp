package com.qoobot.qooerp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典VO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "字典VO")
public class SystemDictVO {

    @Schema(description = "字典ID")
    private Long id;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

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

    @Schema(description = "字典项列表")
    private List<SystemDictItemVO> items;
}
