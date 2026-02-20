package com.qoobot.qooerp.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.file.dto.FileLogDTO;

/**
 * 文件操作日志服务接口
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
public interface FileLogService {

    /**
     * 记录操作日志
     *
     * @param fileId 文件ID
     * @param operation 操作类型
     * @param operationDesc 操作描述
     */
    void log(Long fileId, String operation, String operationDesc);

    /**
     * 分页查询日志
     *
     * @param fileId 文件ID
     * @param operation 操作类型
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<FileLogDTO> listLogs(Long fileId, String operation, Integer pageNum, Integer pageSize);
}
