package com.qoobot.qooerp.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 字典项DTO
 */
@Data
public class SystemDictItemDTO {

    /**
     * 字典项ID
     */
    private Long id;

    /**
     * 字典ID
     */
    private Long dictId;

    /**
     * 字典项标签
     */
    private String itemLabel;

    /**
     * 字典项值
     */
    private String itemValue;

    /**
     * 字典项编码
     */
    private String itemCode;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updateBy;
}
