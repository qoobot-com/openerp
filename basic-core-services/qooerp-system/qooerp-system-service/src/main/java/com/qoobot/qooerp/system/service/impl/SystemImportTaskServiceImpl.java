package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.system.dto.SystemImportTaskDTO;
import com.qoobot.qooerp.system.dto.SystemImportTaskQueryDTO;
import com.qoobot.qooerp.system.entity.SystemImportError;
import com.qoobot.qooerp.system.entity.SystemImportTask;
import com.qoobot.qooerp.system.mapper.SystemImportErrorMapper;
import com.qoobot.qooerp.system.mapper.SystemImportTaskMapper;
import com.qoobot.qooerp.system.service.SystemImportTaskService;
import com.qoobot.qooerp.system.util.ExcelUtils;
import com.qoobot.qooerp.system.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 导入任务Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemImportTaskServiceImpl extends ServiceImpl<SystemImportTaskMapper, SystemImportTask>
        implements SystemImportTaskService {

    private final SystemImportTaskMapper importTaskMapper;
    private final SystemImportErrorMapper importErrorMapper;
    private final FileUtils fileUtils;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemImportTask create(SystemImportTaskDTO dto, Long creatorId, String creatorName) {
        // 保存文件
        MultipartFile file = dto.getFile();
        String relativePath = null;

        if (file != null && !file.isEmpty()) {
            try {
                relativePath = fileUtils.uploadFile(file, "import");
            } catch (IOException e) {
                throw new BusinessException("文件上传失败: " + e.getMessage());
            }
        }

        SystemImportTask task = new SystemImportTask();
        task.setTaskName(dto.getTaskName());
        task.setModuleName(dto.getModuleName());
        task.setImportType(dto.getImportType());
        task.setFilePath(relativePath);
        task.setFileName(file != null ? file.getOriginalFilename() : null);
        task.setStatus(0);
        task.setCreatorId(creatorId);
        task.setCreatorName(creatorName);
        save(task);

        // 异步执行导入任务
        executeImportAsync(task.getId());

        return task;
    }

    @Override
    public IPage<SystemImportTask> page(SystemImportTaskQueryDTO query) {
        LambdaQueryWrapper<SystemImportTask> wrapper = new LambdaQueryWrapper<>();

        if (query.getModuleName() != null) {
            wrapper.like(SystemImportTask::getModuleName, query.getModuleName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SystemImportTask::getStatus, query.getStatus());
        }
        if (query.getCreatorId() != null) {
            wrapper.eq(SystemImportTask::getCreatorId, query.getCreatorId());
        }

        wrapper.orderByDesc(SystemImportTask::getCreateTime);

        return importTaskMapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<SystemImportError> getImportErrors(Long taskId) {
        LambdaQueryWrapper<SystemImportError> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemImportError::getImportTaskId, taskId);
        wrapper.orderByAsc(SystemImportError::getRowNumber);
        return importErrorMapper.selectList(wrapper);
    }

    @Override
    public SystemImportTask getById(Long id) {
        return super.getById(id);
    }

    @Override
    public void downloadTemplate(String moduleName, HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = moduleName + "_template_" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName);

        try {
            // TODO: 根据模块名称生成Excel模板文件
            // 示例：使用EasyExcel或POI生成带有表头的空模板
            // 可以根据不同的模块返回不同的模板
            // 这里创建一个简单的空模板

            // 这里可以使用ExcelUtils.exportExcel方法
            // response.getOutputStream().flush();

            log.info("下载导入模板: moduleName={}, fileName={}", moduleName, fileName);

        } catch (Exception e) {
            log.error("模板下载失败: moduleName={}, error={}", moduleName, e.getMessage(), e);
            throw new RuntimeException("模板下载失败", e);
        }
    }

    /**
     * 异步执行导入任务
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void executeImportAsync(Long taskId) {
        SystemImportTask task = getById(taskId);
        if (task == null) {
            return;
        }

        try {
            // 更新状态为执行中
            task.setStatus(1);
            updateById(task);

            // 验证文件是否存在
            if (task.getFilePath() == null || task.getFilePath().isEmpty()) {
                throw new BusinessException("导入文件路径为空");
            }

            String fullPath = fileUtils.getFullPath(task.getFilePath());
            if (fullPath == null) {
                throw new BusinessException("无法获取导入文件路径");
            }

            if (!fileUtils.fileExists(task.getFilePath())) {
                throw new BusinessException("导入文件不存在");
            }

            // TODO: 根据模块名称执行实际的导入逻辑
            // 示例：读取Excel/CSV文件，验证数据，插入数据库
            // 这里需要根据不同的模块实现不同的导入逻辑
            // 可以使用策略模式或工厂模式来处理不同模块的导入

            // 模拟导入过程
            int successCount = 0;
            int failureCount = 0;
            int totalRecords = 0;

            // 清除之前的错误记录
            LambdaQueryWrapper<SystemImportError> errorWrapper = new LambdaQueryWrapper<>();
            errorWrapper.eq(SystemImportError::getImportTaskId, taskId);
            importErrorMapper.delete(errorWrapper);

            // 示例：使用ExcelUtils读取Excel文件
            // List<ImportData> dataList = ExcelUtils.importExcel(file, ImportData.class);

            // 遍历数据并插入
            // for (int i = 0; i < dataList.size(); i++) {
            //     ImportData data = dataList.get(i);
            //     try {
            //         // 验证数据
            //         validateData(data);
            //         // 插入数据库
            //         insertData(data);
            //         successCount++;
            //     } catch (Exception e) {
            //         // 记录错误
            //         SystemImportError error = new SystemImportError();
            //         error.setImportTaskId(taskId);
            //         error.setRowNumber(i + 2); // Excel从第2行开始（第1行是表头）
            //         error.setFieldName("全部字段");
            //         error.setErrorMessage(e.getMessage());
            //         error.setRowData(toJson(data));
            //         importErrorMapper.insert(error);
            //         failureCount++;
            //     }
            //     totalRecords++;
            // }

            // 更新状态为成功
            task.setStatus(2);
            task.setCompleteTime(LocalDateTime.now());
            task.setTotalRecords(totalRecords);
            task.setSuccessRecords(successCount);
            task.setFailureRecords(failureCount);
            updateById(task);

            log.info("导入任务执行成功: taskId={}, totalRecords={}, success={}, failure={}",
                    taskId, totalRecords, successCount, failureCount);

        } catch (Exception e) {
            log.error("导入任务执行失败: taskId={}, error={}", taskId, e.getMessage(), e);

            // 更新状态为失败
            task.setStatus(3);
            task.setCompleteTime(LocalDateTime.now());
            updateById(task);
        }
    }

    /**
     * 执行导入任务（同步，用于测试）
     */
    @Deprecated
    public void executeImport(Long taskId) {
        executeImportAsync(taskId);
    }
}
