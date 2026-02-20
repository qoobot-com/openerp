package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 导入错误记录实体类
 */
@Data
@TableName("system_import_error")
public class SystemImportError {

    /**
     * 错误ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 导入任务ID
     */
    private Long importTaskId;

    /**
     * 行号
     */
    private Integer rowNumber;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 行数据（JSON格式）
     */
    private String rowData;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
