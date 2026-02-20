package com.qoobot.qooerp.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统参数DTO
 */
@Data
public class SystemConfigDTO {

    /**
     * 参数ID
     */
    private Long id;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键
     */
    private String configKey;

    /**
     * 参数值
     */
    private String configValue;

    /**
     * 参数类型：string/number/boolean
     */
    private String configType;

    /**
     * 是否系统参数：0-否 1-是
     */
    private Integer isSystem;

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
