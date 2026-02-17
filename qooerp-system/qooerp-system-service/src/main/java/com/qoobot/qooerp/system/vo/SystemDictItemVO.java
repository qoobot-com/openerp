package com.qoobot.qooerp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典项VO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "字典项VO")
public class SystemDictItemVO {

    @Schema(description = "字典项ID")
    private Long id;

    @Schema(description = "字典ID")
    private Long dictId;

    @Schema(description = "字典项标签")
    private String itemLabel;

    @Schema(description = "字典项值")
    private String itemValue;

    @Schema(description = "字典项编码")
    private String itemCode;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
