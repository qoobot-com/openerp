package com.qoobot.qooerp.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件操作日志实体
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
@TableName("file_log")
public class FileLog {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 操作类型（upload/download/delete/rename/move/share）
     */
    private String operation;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作IP
     */
    private String operatorIp;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 租户ID
     */
    private Long tenantId;
}
