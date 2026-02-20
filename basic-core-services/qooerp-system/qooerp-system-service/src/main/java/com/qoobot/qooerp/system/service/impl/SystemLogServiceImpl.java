package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.system.dto.SystemLogDTO;
import com.qoobot.qooerp.system.dto.SystemLogQueryDTO;
import com.qoobot.qooerp.system.entity.SystemLog;
import com.qoobot.qooerp.system.mapper.SystemLogMapper;
import com.qoobot.qooerp.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 */
@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements SystemLogService {

    private final SystemLogMapper logMapper;

    @Override
    public SystemLogDTO getLog(Long id) {
        SystemLog log = logMapper.selectById(id);
        if (log == null) {
            throw new BusinessException("日志不存在");
        }

        SystemLogDTO dto = new SystemLogDTO();
        org.springframework.beans.BeanUtils.copyProperties(log, dto);
        return dto;
    }

    @Override
    public IPage<SystemLogDTO> pageLog(Integer current, Integer size, SystemLogQueryDTO queryDTO) {
        Page<SystemLog> page = new Page<>(current, size);
        return logMapper.selectPageByQuery(page, queryDTO);
    }

    @Override
    public Map<String, Object> getStatistics(SystemLogQueryDTO queryDTO) {
        return logMapper.selectStatistics(queryDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLog(Long id) {
        SystemLog log = logMapper.selectById(id);
        if (log == null) {
            throw new BusinessException("日志不存在");
        }
        logMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteLog(String ids) {
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("日志ID不能为空");
        }

        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        logMapper.deleteBatchIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(SystemLog log) {
        logMapper.insert(log);
    }
}
