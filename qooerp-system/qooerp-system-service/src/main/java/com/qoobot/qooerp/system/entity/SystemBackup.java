package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统备份实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_backup")
public class SystemBackup extends BaseEntity {

    /**
     * 备份ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 备份类型：FULL-全量 INCREMENTAL-增量 DIFFERENTIAL-差异
     */
    private String backupType;

    /**
     * 备份名称
     */
    private String backupName;

    /**
     * 备份文件路径
     */
    private String filePath;

    /**
     * 备份文件大小（字节）
     */
    private Long fileSize;

    /**
     * 备份状态：PENDING-进行中 SUCCESS-成功 FAILED-失败
     */
    private String status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 备份耗时（毫秒）
     */
    private Long duration;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 备份描述
     */
    private String description;

    /**
     * 是否可恢复
     */
    private Boolean recoverable;

    /**
     * 保留天数
     */
    private Integer retentionDays;
}
