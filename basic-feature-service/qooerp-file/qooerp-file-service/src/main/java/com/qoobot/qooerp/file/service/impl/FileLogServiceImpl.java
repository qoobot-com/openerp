package com.qoobot.qooerp.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.file.dto.FileLogDTO;
import com.qoobot.qooerp.file.entity.FileInfo;
import com.qoobot.qooerp.file.entity.FileLog;
import com.qoobot.qooerp.file.mapper.FileInfoMapper;
import com.qoobot.qooerp.file.mapper.FileLogMapper;
import com.qoobot.qooerp.file.service.FileLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件操作日志服务实现
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileLogServiceImpl implements FileLogService {

    private final FileLogMapper logMapper;
    private final FileInfoMapper fileInfoMapper;

    @Override
    public void log(Long fileId, String operation, String operationDesc) {
        FileLog log = new FileLog();
        log.setFileId(fileId);
        log.setOperation(operation);
        log.setOperationDesc(operationDesc);
        log.setOperatorId(getCurrentUserId());
        log.setOperatorName(getCurrentUserName());
        log.setOperatorIp(getClientIp());
        log.setUserAgent(getUserAgent());
        log.setOperationTime(LocalDateTime.now());
        log.setTenantId(TenantContextHolder.getTenantId());
        logMapper.insert(log);
    }

    @Override
    public PageResult<FileLogDTO> listLogs(Long fileId, String operation, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FileLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileLog::getTenantId, TenantContextHolder.getTenantId());
        wrapper.eq(fileId != null, FileLog::getFileId, fileId);
        wrapper.eq(operation != null, FileLog::getOperation, operation);
        wrapper.orderByDesc(FileLog::getOperationTime);

        Page<FileLog> page = new Page<>(pageNum, pageSize);
        Page<FileLog> result = logMapper.selectPage(page, wrapper);

        List<FileLogDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(page.getCurrent(), page.getSize(), result.getTotal(), dtoList);
    }

    private FileLogDTO convertToDTO(FileLog log) {
        FileLogDTO dto = BeanUtils.copyBean(log, FileLogDTO.class);

        // 查询文件名称
        if (log.getFileId() != null) {
            FileInfo file = fileInfoMapper.selectById(log.getFileId());
            if (file != null) {
                dto.setFileName(file.getFileName());
            }
        }

        return dto;
    }

    private Long getCurrentUserId() {
        try {
            return SecurityUtils.getCurrentUserId();
        } catch (Exception e) {
            return 1L;
        }
    }

    private String getCurrentUserName() {
        try {
            return SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            return "系统";
        }
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            log.error("获取客户端IP失败", e);
        }
        return "127.0.0.1";
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.error("获取User-Agent失败", e);
        }
        return "";
    }
}
