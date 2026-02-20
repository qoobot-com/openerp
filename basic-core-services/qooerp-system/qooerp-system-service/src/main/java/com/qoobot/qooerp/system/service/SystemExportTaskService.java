package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemExportTaskDTO;
import com.qoobot.qooerp.system.dto.SystemExportTaskQueryDTO;
import com.qoobot.qooerp.system.entity.SystemExportTask;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 导出任务Service
 */
public interface SystemExportTaskService extends IService<SystemExportTask> {

    /**
     * 创建导出任务
     */
    SystemExportTask create(SystemExportTaskDTO dto, Long creatorId, String creatorName);

    /**
     * 分页查询导出任务
     */
    IPage<SystemExportTask> page(SystemExportTaskQueryDTO query);

    /**
     * 下载导出文件
     */
    void downloadFile(Long id, HttpServletResponse response);

    /**
     * 删除导出任务
     */
    void delete(Long id);

    /**
     * 获取导出任务详情
     */
    SystemExportTask getById(Long id);
}
