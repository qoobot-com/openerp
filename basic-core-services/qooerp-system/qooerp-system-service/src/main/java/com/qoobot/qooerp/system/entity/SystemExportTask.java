package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据导出任务实体类
 */
@Data
@TableName("system_export_task")
public class SystemExportTask {

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 导出类型：excel/csv/pdf
     */
    private String exportType;

    /**
     * 导出条件（JSON格式）
     */
    private String exportCondition;

    /**
     * 状态：0-待执行，1-执行中，2-成功，3-失败
     */
    private Integer status;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 总记录数
     */
    private Integer totalRecords;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 错误信息
     */
    private String errorMessage;
}
