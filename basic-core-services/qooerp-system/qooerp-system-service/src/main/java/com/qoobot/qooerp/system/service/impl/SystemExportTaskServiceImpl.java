package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.system.dto.SystemExportTaskDTO;
import com.qoobot.qooerp.system.dto.SystemExportTaskQueryDTO;
import com.qoobot.qooerp.system.entity.SystemExportTask;
import com.qoobot.qooerp.system.mapper.SystemExportTaskMapper;
import com.qoobot.qooerp.system.service.SystemExportTaskService;
import com.qoobot.qooerp.system.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 导出任务Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemExportTaskServiceImpl extends ServiceImpl<SystemExportTaskMapper, SystemExportTask>
        implements SystemExportTaskService {

    private final SystemExportTaskMapper exportTaskMapper;
    private final FileUtils fileUtils;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemExportTask create(SystemExportTaskDTO dto, Long creatorId, String creatorName) {
        SystemExportTask task = new SystemExportTask();
        task.setTaskName(dto.getTaskName());
        task.setModuleName(dto.getModuleName());
        task.setExportType(dto.getExportType());
        task.setExportCondition(dto.getExportCondition());
        task.setStatus(0);
        task.setCreatorId(creatorId);
        task.setCreatorName(creatorName);
        save(task);

        // 异步执行导出任务
        executeExportAsync(task.getId());

        return task;
    }

    @Override
    public IPage<SystemExportTask> page(SystemExportTaskQueryDTO query) {
        LambdaQueryWrapper<SystemExportTask> wrapper = new LambdaQueryWrapper<>();

        if (query.getModuleName() != null) {
            wrapper.like(SystemExportTask::getModuleName, query.getModuleName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SystemExportTask::getStatus, query.getStatus());
        }
        if (query.getCreatorId() != null) {
            wrapper.eq(SystemExportTask::getCreatorId, query.getCreatorId());
        }

        wrapper.orderByDesc(SystemExportTask::getCreateTime);

        return exportTaskMapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public void downloadFile(Long id, HttpServletResponse response) {
        SystemExportTask task = getById(id);
        if (task == null) {
            throw new RuntimeException("导出任务不存在");
        }

        if (task.getFilePath() == null || task.getFilePath().isEmpty()) {
            throw new RuntimeException("导出文件路径为空");
        }

        String fullPath = fileUtils.getFullPath(task.getFilePath());
        if (fullPath == null) {
            throw new RuntimeException("无法获取文件路径");
        }

        File file = new File(fullPath);
        if (!file.exists()) {
            throw new RuntimeException("导出文件不存在");
        }

        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(task.getFileName(), java.nio.charset.StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName);

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            log.error("文件下载失败: taskId={}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SystemExportTask task = getById(id);
        if (task != null) {
            // 删除文件
            if (task.getFilePath() != null) {
                fileUtils.deleteFile(task.getFilePath());
            }
            // 删除记录
            removeById(id);
        }
    }

    @Override
    public SystemExportTask getById(Long id) {
        return super.getById(id);
    }

    /**
     * 异步执行导出任务
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void executeExportAsync(Long taskId) {
        SystemExportTask task = getById(taskId);
        if (task == null) {
            return;
        }

        try {
            // 更新状态为执行中
            task.setStatus(1);
            updateById(task);

            // 生成文件名
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            String fileName = task.getTaskName() + "_" + timestamp + "." + task.getExportType();

            // 生成文件路径
            String relativePath = "export/" + task.getModuleName() + "/" + fileName;
            String fullPath = fileUtils.getFullPath(relativePath);

            // 确保目录存在
            Files.createDirectories(Paths.get(fullPath).getParent());

            // TODO: 根据模块名称和导出条件执行实际的导出逻辑
            // 示例：查询数据、生成Excel/CSV/PDF文件
            // 这里需要根据不同的模块实现不同的导出逻辑
            // 可以使用策略模式或工厂模式来处理不同模块的导出

            // 模拟导出数据
            int totalRecords = 0;
            long fileSize = 0;

            // 更新状态为成功
            task.setStatus(2);
            task.setCompleteTime(LocalDateTime.now());
            task.setFilePath(relativePath);
            task.setFileName(fileName);
            task.setFileSize(fileSize);
            task.setTotalRecords(totalRecords);
            updateById(task);

            log.info("导出任务执行成功: taskId={}, fileName={}, totalRecords={}", taskId, fileName, totalRecords);

        } catch (Exception e) {
            log.error("导出任务执行失败: taskId={}, error={}", taskId, e.getMessage(), e);

            // 更新状态为失败
            task.setStatus(3);
            task.setErrorMessage(e.getMessage());
            task.setCompleteTime(LocalDateTime.now());
            updateById(task);
        }
    }

    /**
     * 执行导出任务（同步，用于测试）
     */
    @Deprecated
    public void executeExport(Long taskId) {
        executeExportAsync(taskId);
    }
}
