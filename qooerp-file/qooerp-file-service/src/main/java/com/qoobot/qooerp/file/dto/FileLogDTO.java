package com.qoobot.qooerp.file.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件操作日志DTO
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
public class FileLogDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 操作类型
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
     * 操作时间
     */
    private LocalDateTime operationTime;
}
