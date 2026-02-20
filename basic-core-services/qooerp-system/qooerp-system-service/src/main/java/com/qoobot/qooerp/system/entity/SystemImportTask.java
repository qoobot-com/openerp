package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据导入任务实体类
 */
@Data
@TableName("system_import_task")
public class SystemImportTask {

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
     * 导入类型：excel/csv
     */
    private String importType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 总记录数
     */
    private Integer totalRecords;

    /**
     * 成功记录数
     */
    private Integer successRecords;

    /**
     * 失败记录数
     */
    private Integer failureRecords;

    /**
     * 状态：0-待执行，1-执行中，2-成功，3-失败
     */
    private Integer status;

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
}
