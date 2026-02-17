package com.qoobot.qooerp.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.notify.dto.NotifyRecordDTO;
import com.qoobot.qooerp.notify.dto.NotifyRecordQueryDTO;
import com.qoobot.qooerp.notify.dto.StatisticsDTO;
import com.qoobot.qooerp.notify.entity.NotifyRecord;
import com.qoobot.qooerp.notify.enums.NotifyStatusEnum;
import com.qoobot.qooerp.notify.mapper.NotifyRecordMapper;
import com.qoobot.qooerp.notify.service.NotifyService;
import com.qoobot.qooerp.notify.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 记录服务实现
 */
@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final NotifyRecordMapper recordMapper;
    private final NotifyService notifyService;

    @Override
    public Page<NotifyRecordDTO> page(NotifyRecordQueryDTO dto) {
        LambdaQueryWrapper<NotifyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyRecord::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getNotifyNo() != null) {
            wrapper.like(NotifyRecord::getNotifyNo, dto.getNotifyNo());
        }
        if (dto.getType() != null) {
            wrapper.eq(NotifyRecord::getType, dto.getType());
        }
        if (dto.getReceiver() != null) {
            wrapper.like(NotifyRecord::getReceiver, dto.getReceiver());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(NotifyRecord::getStatus, dto.getStatus());
        }
        if (dto.getTemplateId() != null) {
            wrapper.eq(NotifyRecord::getTemplateId, dto.getTemplateId());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(NotifyRecord::getCreateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(NotifyRecord::getCreateTime, dto.getEndTime());
        }

        wrapper.orderByDesc(NotifyRecord::getCreateTime);

        Page<NotifyRecord> page = new Page<>(dto.getCurrent(), dto.getSize());
        page = recordMapper.selectPage(page, wrapper);

        Page<NotifyRecordDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToDTO).toList());
        return result;
    }

    @Override
    public NotifyRecordDTO get(Long id) {
        NotifyRecord record = recordMapper.selectById(id);
        if (record == null) {
            return null;
        }
        return convertToDTO(record);
    }

    @Override
    public void retry(Long id) {
        NotifyRecord record = recordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("通知记录不存在");
        }

        if (!record.getStatus().equals(NotifyStatusEnum.FAILED)) {
            throw new RuntimeException("只能重试失败的通知");
        }

        if (record.getRetryCount() >= 3) {
            throw new RuntimeException("重试次数已达上限");
        }

        record.setRetryCount(record.getRetryCount() + 1);
        recordMapper.updateById(record);

        // TODO: 调用发送服务重新发送
        notifyService.resend(record);
    }

    @Override
    public Map<String, Object> statistics(StatisticsDTO dto) {
        LambdaQueryWrapper<NotifyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyRecord::getTenantId, TenantContextHolder.getTenantId());

        if (dto.getStartTime() != null) {
            wrapper.ge(NotifyRecord::getCreateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(NotifyRecord::getCreateTime, dto.getEndTime());
        }
        if (dto.getType() != null) {
            wrapper.eq(NotifyRecord::getType, dto.getType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(NotifyRecord::getStatus, dto.getStatus());
        }

        Long totalCount = recordMapper.selectCount(wrapper);
        Long successCount = recordMapper.selectCount(wrapper.eq(NotifyRecord::getStatus, NotifyStatusEnum.SUCCESS));
        Long failedCount = recordMapper.selectCount(wrapper.eq(NotifyRecord::getStatus, NotifyStatusEnum.FAILED));
        Long pendingCount = recordMapper.selectCount(wrapper.eq(NotifyRecord::getStatus, NotifyStatusEnum.PENDING));

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        result.put("pendingCount", pendingCount);
        result.put("successRate", totalCount > 0 ? (successCount * 100.0 / totalCount) : 0);

        return result;
    }

    private NotifyRecordDTO convertToDTO(NotifyRecord entity) {
        NotifyRecordDTO dto = new NotifyRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
