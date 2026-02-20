package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemImportTaskDTO;
import com.qoobot.qooerp.system.dto.SystemImportTaskQueryDTO;
import com.qoobot.qooerp.system.entity.SystemImportError;
import com.qoobot.qooerp.system.entity.SystemImportTask;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 导入任务Service
 */
public interface SystemImportTaskService extends IService<SystemImportTask> {

    /**
     * 创建导入任务
     */
    SystemImportTask create(SystemImportTaskDTO dto, Long creatorId, String creatorName);

    /**
     * 分页查询导入任务
     */
    IPage<SystemImportTask> page(SystemImportTaskQueryDTO query);

    /**
     * 获取导入错误详情
     */
    List<SystemImportError> getImportErrors(Long taskId);

    /**
     * 获取导入任务详情
     */
    SystemImportTask getById(Long id);

    /**
     * 下载导入模板
     */
    void downloadTemplate(String moduleName, HttpServletResponse response);
}
