package com.qoobot.qooerp.scm.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.logistics.domain.LogisticsTracking;
import com.qoobot.qooerp.scm.logistics.dto.LogisticsTrackingDTO;
import com.qoobot.qooerp.scm.logistics.dto.TrackingQueryDTO;
import com.qoobot.qooerp.scm.logistics.mapper.LogisticsTrackingMapper;
import com.qoobot.qooerp.scm.logistics.service.ILogisticsTrackingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 物流跟踪Service实现
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Service
public class LogisticsTrackingServiceImpl extends ServiceImpl<LogisticsTrackingMapper, LogisticsTracking>
        implements ILogisticsTrackingService {

    private static final String TRACKING_NUMBER_PREFIX = "TRK";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(LogisticsTrackingDTO dto) {
        // 生成运单号
        if (!StringUtils.hasText(dto.getTrackingNumber())) {
            dto.setTrackingNumber(generateTrackingNumber());
        }

        // 校验运单号唯一性
        if (checkTrackingNumberExists(dto.getTrackingNumber(), null)) {
            throw new BusinessException("运单号已存在：" + dto.getTrackingNumber());
        }

        // 设置默认状态
        if (!StringUtils.hasText(dto.getStatus())) {
            dto.setStatus("PENDING");
        }

        LogisticsTracking tracking = new LogisticsTracking();
        BeanUtils.copyProperties(dto, tracking);

        save(tracking);
        return tracking.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(LogisticsTrackingDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("物流跟踪ID不能为空");
        }

        LogisticsTracking tracking = getById(dto.getId());
        if (tracking == null) {
            throw new BusinessException("物流跟踪不存在");
        }

        // 如果修改了运单号，需要校验唯一性
        if (StringUtils.hasText(dto.getTrackingNumber()) &&
            !dto.getTrackingNumber().equals(tracking.getTrackingNumber())) {
            if (checkTrackingNumberExists(dto.getTrackingNumber(), dto.getId())) {
                throw new BusinessException("运单号已存在：" + dto.getTrackingNumber());
            }
        }

        // 如果状态变更为已签收，更新实际送达时间
        if ("DELIVERED".equals(dto.getStatus()) && !"DELIVERED".equals(tracking.getStatus())) {
            dto.setActualDelivery(LocalDateTime.now());
        }

        BeanUtils.copyProperties(dto, tracking);
        return updateById(tracking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        LogisticsTracking tracking = getById(id);
        if (tracking == null) {
            throw new BusinessException("物流跟踪不存在");
        }

        // TODO: 检查是否有关联的物流费用
        // if (hasRelatedCosts(id)) {
        //     throw new BusinessException("该物流跟踪下存在关联的费用，不能删除");
        // }

        return removeById(id);
    }

    @Override
    public LogisticsTrackingDTO getDetail(Long id) {
        LogisticsTracking tracking = getById(id);
        if (tracking == null) {
            throw new BusinessException("物流跟踪不存在");
        }

        LogisticsTrackingDTO dto = new LogisticsTrackingDTO();
        BeanUtils.copyProperties(tracking, dto);
        return dto;
    }

    @Override
    public PageResult<LogisticsTracking> queryPage(TrackingQueryDTO queryDTO) {
        LambdaQueryWrapper<LogisticsTracking> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getTrackingNumber())) {
            wrapper.like(LogisticsTracking::getTrackingNumber, queryDTO.getTrackingNumber());
        }
        if (queryDTO.getOrderId() != null) {
            wrapper.eq(LogisticsTracking::getOrderId, queryDTO.getOrderId());
        }
        if (StringUtils.hasText(queryDTO.getCarrierCode())) {
            wrapper.eq(LogisticsTracking::getCarrierCode, queryDTO.getCarrierCode());
        }
        if (StringUtils.hasText(queryDTO.getReceiver())) {
            wrapper.like(LogisticsTracking::getReceiver, queryDTO.getReceiver());
        }
        if (StringUtils.hasText(queryDTO.getReceiverPhone())) {
            wrapper.like(LogisticsTracking::getReceiverPhone, queryDTO.getReceiverPhone());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(LogisticsTracking::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getShipmentDateStart() != null) {
            wrapper.ge(LogisticsTracking::getShipmentDate, queryDTO.getShipmentDateStart());
        }
        if (queryDTO.getShipmentDateEnd() != null) {
            wrapper.le(LogisticsTracking::getShipmentDate, queryDTO.getShipmentDateEnd());
        }
        if (queryDTO.getEstimatedDeliveryStart() != null) {
            wrapper.ge(LogisticsTracking::getEstimatedDelivery, queryDTO.getEstimatedDeliveryStart());
        }
        if (queryDTO.getEstimatedDeliveryEnd() != null) {
            wrapper.le(LogisticsTracking::getEstimatedDelivery, queryDTO.getEstimatedDeliveryEnd());
        }

        wrapper.orderByDesc(LogisticsTracking::getCreateTime);

        Page<LogisticsTracking> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        Page<LogisticsTracking> result = page(page, wrapper);

        return new PageResult<>(page.getCurrent(), page.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public LogisticsTrackingDTO getByTrackingNumber(String trackingNumber) {
        LambdaQueryWrapper<LogisticsTracking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogisticsTracking::getTrackingNumber, trackingNumber);
        LogisticsTracking tracking = getOne(wrapper);

        if (tracking == null) {
            throw new BusinessException("物流跟踪不存在");
        }

        LogisticsTrackingDTO dto = new LogisticsTrackingDTO();
        BeanUtils.copyProperties(tracking, dto);
        return dto;
    }

    @Override
    public LogisticsTrackingDTO getByOrderId(Long orderId) {
        LambdaQueryWrapper<LogisticsTracking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogisticsTracking::getOrderId, orderId);
        LogisticsTracking tracking = getOne(wrapper);

        if (tracking == null) {
            throw new BusinessException("物流跟踪不存在");
        }

        LogisticsTrackingDTO dto = new LogisticsTrackingDTO();
        BeanUtils.copyProperties(tracking, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        LogisticsTracking tracking = getById(id);
        if (tracking == null) {
            throw new BusinessException("物流跟踪不存在");
        }

        tracking.setStatus(status);

        // 如果状态变更为已签收，更新实际送达时间
        if ("DELIVERED".equals(status) && !"DELIVERED".equals(tracking.getStatus())) {
            tracking.setActualDelivery(LocalDateTime.now());
        }

        return updateById(tracking);
    }

    @Override
    public String generateTrackingNumber() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String prefix = TRACKING_NUMBER_PREFIX + dateStr;

        LambdaQueryWrapper<LogisticsTracking> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(LogisticsTracking::getTrackingNumber, prefix);
        wrapper.orderByDesc(LogisticsTracking::getTrackingNumber);
        wrapper.last("LIMIT 1");

        LogisticsTracking lastTracking = getOne(wrapper);

        int seq = 1;
        if (lastTracking != null && StringUtils.hasText(lastTracking.getTrackingNumber())) {
            String lastCode = lastTracking.getTrackingNumber();
            try {
                String seqStr = lastCode.substring(prefix.length());
                seq = Integer.parseInt(seqStr) + 1;
            } catch (Exception e) {
                // 解析失败，从1开始
            }
        }

        return String.format("%s%04d", prefix, seq);
    }

    @Override
    public boolean checkTrackingNumberExists(String trackingNumber, Long excludeId) {
        LambdaQueryWrapper<LogisticsTracking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogisticsTracking::getTrackingNumber, trackingNumber);
        if (excludeId != null) {
            wrapper.ne(LogisticsTracking::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLocation(Long id, String currentLocation) {
        LogisticsTracking tracking = getById(id);
        if (tracking == null) {
            throw new BusinessException("物流跟踪不存在");
        }
        tracking.setCurrentLocation(currentLocation);
        return updateById(tracking);
    }
}
