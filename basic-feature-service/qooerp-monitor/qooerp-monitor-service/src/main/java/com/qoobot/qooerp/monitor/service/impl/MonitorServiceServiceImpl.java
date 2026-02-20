package com.qoobot.qooerp.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.entity.MonitorService;
import com.qoobot.qooerp.monitor.mapper.MonitorServiceMapper;
import com.qoobot.qooerp.monitor.service.MonitorServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorServiceServiceImpl implements MonitorServiceService {

    private final MonitorServiceMapper serviceMapper;

    @Override
    public Long registerService(MonitorServiceCreateDTO dto) {
        MonitorService entity = new MonitorService();
        BeanUtils.copyProperties(dto, entity);
        entity.setTenantId(TenantContextHolder.getTenantId());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        if (entity.getHealthStatus() == null) {
            entity.setHealthStatus(1);
        }
        serviceMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateService(Long id, MonitorServiceUpdateDTO dto) {
        MonitorService entity = serviceMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("服务不存在");
        }
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        serviceMapper.updateById(entity);
    }

    @Override
    public void deleteService(Long id) {
        serviceMapper.deleteById(id);
    }

    @Override
    public MonitorServiceDTO getService(Long id) {
        MonitorService entity = serviceMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("服务不存在");
        }
        return convertToDTO(entity);
    }

    @Override
    public List<MonitorServiceDTO> listServices() {
        LambdaQueryWrapper<MonitorService> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorService::getTenantId, TenantContextHolder.getTenantId());
        wrapper.orderByDesc(MonitorService::getCreateTime);
        List<MonitorService> entities = serviceMapper.selectList(wrapper);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void healthCheck(ServiceCheckDTO dto) {
        LambdaQueryWrapper<MonitorService> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorService::getServiceName, dto.getServiceName());
        wrapper.eq(MonitorService::getTenantId, TenantContextHolder.getTenantId());
        MonitorService entity = serviceMapper.selectOne(wrapper);
        if (entity == null) {
            throw new RuntimeException("服务不存在");
        }
        entity.setLastCheckTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        serviceMapper.updateById(entity);
    }

    private MonitorServiceDTO convertToDTO(MonitorService entity) {
        MonitorServiceDTO dto = new MonitorServiceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
