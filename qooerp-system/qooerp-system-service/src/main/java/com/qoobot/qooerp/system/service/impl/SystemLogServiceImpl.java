package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.util.IpUtils;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.system.dto.SystemLogDTO;
import com.qoobot.qooerp.system.entity.SystemLog;
import com.qoobot.qooerp.system.mapper.SystemLogMapper;
import com.qoobot.qooerp.system.service.SystemLogService;
import com.qoobot.qooerp.system.vo.SystemLogVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements SystemLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createLog(SystemLog systemLog) {
        baseMapper.insert(systemLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveLog(SystemLogDTO dto) {
        SystemLog systemLog = new SystemLog();
        BeanUtils.copyProperties(dto, systemLog);

        // 获取当前用户信息
        systemLog.setUserId(SecurityUtils.getUserIdOrDefault(0L));
        systemLog.setUsername(SecurityUtils.getUsernameOrDefault("system"));

        // 获取请求信息
        try {
            HttpServletRequest request = getRequest();
            if (request != null) {
                String ip = IpUtils.getIpAddress(request);
                systemLog.setIp(ip);
                systemLog.setLocation("本地"); // 临时默认值
                String userAgent = request.getHeader("User-Agent");
                systemLog.setBrowser(parseBrowser(userAgent));
                systemLog.setOs(parseOs(userAgent));
            }
        } catch (Exception e) {
            log.warn("获取请求信息失败", e);
        }

        if (systemLog.getStatus() == null) {
            systemLog.setStatus(1);
        }

        baseMapper.insert(systemLog);
        return systemLog.getId();
    }

    @Override
    @Async
    public void saveLogAsync(SystemLogDTO dto) {
        saveLog(dto);
    }

    @Override
    public SystemLogVO getLog(Long id) {
        SystemLog log = baseMapper.selectById(id);
        if (log == null) {
            return null;
        }
        SystemLogVO vo = new SystemLogVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }

    @Override
    public Page<SystemLogVO> pageLog(Long current, Long size, String logType, String module,
                                        String username, String ip, Integer status,
                                        String startTime, String endTime) {
        Page<SystemLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();

        // logType字段在SystemLog实体中不存在，使用module字段替代
        if (logType != null && !logType.isEmpty()) {
            wrapper.like(SystemLog::getModule, logType);
        }
        if (module != null && !module.isEmpty()) {
            wrapper.like(SystemLog::getModule, module);
        }
        if (username != null && !username.isEmpty()) {
            wrapper.like(SystemLog::getUsername, username);
        }
        if (ip != null && !ip.isEmpty()) {
            wrapper.like(SystemLog::getIp, ip);
        }
        if (status != null) {
            wrapper.eq(SystemLog::getStatus, status);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(SystemLog::getOperateTime, LocalDateTime.parse(startTime, formatter));
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(SystemLog::getOperateTime, LocalDateTime.parse(endTime, formatter));
        }

        wrapper.orderByDesc(SystemLog::getOperateTime);

        Page<SystemLog> resultPage = baseMapper.selectPage(page, wrapper);

        Page<SystemLogVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<SystemLogVO> voList = resultPage.getRecords().stream().map(log -> {
            SystemLogVO vo = new SystemLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public Object statistics(String startDate, String endDate) {
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(SystemLog::getOperateTime, LocalDateTime.parse(startDate, formatter));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(SystemLog::getOperateTime, LocalDateTime.parse(endDate, formatter));
        }

        List<SystemLog> logs = baseMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("totalLogs", logs.size());
        result.put("successLogs", logs.stream().filter(l -> l.getStatus() == 1).count());
        result.put("failLogs", logs.stream().filter(l -> l.getStatus() == 0).count());
        
        double avgCostTime = logs.stream()
                .filter(l -> l.getCostTime() != null)
                .mapToInt(SystemLog::getCostTime)
                .average()
                .orElse(0.0);
        result.put("avgCostTime", avgCostTime);

        double successRate = logs.isEmpty() ? 0 : 
                (double) logs.stream().filter(l -> l.getStatus() == 1).count() / logs.size() * 100;
        result.put("successRate", successRate);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteLog(List<Long> ids) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpireLogs(Integer days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SystemLog::getOperateTime, expireTime);
        return baseMapper.delete(wrapper);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) return "Unknown";
        
        if (userAgent.contains("Edg/")) {  // Edge新版本
            return "Edge";
        } else if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return "Safari";
        } else if (userAgent.contains("Edge")) {  // Edge旧版本
            return "Edge";
        } else {
            return "Unknown";
        }
    }

    private String parseOs(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) return "Unknown";
        
        if (userAgent.contains("Windows NT")) {
            return "Windows";
        } else if (userAgent.contains("Mac OS X")) {
            return "macOS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone OS")) {
            return "iOS";
        } else if (userAgent.contains("iPad")) {
            return "iOS";
        } else {
            return "Unknown";
        }
    }
}
