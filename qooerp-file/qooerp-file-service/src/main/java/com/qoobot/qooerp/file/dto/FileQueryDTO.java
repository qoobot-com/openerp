package com.qoobot.qooerp.file.dto;

import com.qoobot.qooerp.common.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件查询DTO
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileQueryDTO extends PageQueryDTO {

    /**
     * 文件夹ID
     */
    private Long folderId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
