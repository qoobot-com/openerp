package com.qoobot.qooerp.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.report.dto.ReportSubscriptionCreateDTO;
import com.qoobot.qooerp.report.dto.ReportSubscriptionDTO;
import com.qoobot.qooerp.report.dto.ReportSubscriptionQueryDTO;
import com.qoobot.qooerp.report.dto.ReportSubscriptionUpdateDTO;
import com.qoobot.qooerp.report.entity.ReportSubscription;
import com.qoobot.qooerp.report.enums.ReportStatus;
import com.qoobot.qooerp.report.mapper.ReportSubscriptionMapper;
import com.qoobot.qooerp.report.service.ReportSubscriptionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportSubscriptionServiceImpl extends ServiceImpl<ReportSubscriptionMapper, ReportSubscription> implements ReportSubscriptionService {

    @Override
    public ReportSubscriptionDTO create(ReportSubscriptionCreateDTO dto) {
        ReportSubscription subscription = new ReportSubscription();
        BeanUtils.copyProperties(dto, subscription);
        subscription.setStatus(1);
        subscription.setLastRunTime(LocalDateTime.now());
        save(subscription);
        return toDTO(subscription);
    }

    @Override
    public ReportSubscriptionDTO update(Long id, ReportSubscriptionUpdateDTO dto) {
        ReportSubscription subscription = super.getById(id);
        if (subscription == null) {
            throw new BusinessException("报表订阅不存在");
        }
        BeanUtils.copyProperties(dto, subscription);
        updateById(subscription);
        return toDTO(subscription);
    }

    @Override
    public void delete(Long id) {
        ReportSubscription subscription = super.getById(id);
        if (subscription == null) {
            throw new BusinessException("报表订阅不存在");
        }
        removeById(id);
    }

    @Override
    public ReportSubscriptionDTO getById(Long id) {
        ReportSubscription subscription = super.getById(id);
        if (subscription == null) {
            throw new BusinessException("报表订阅不存在");
        }
        return toDTO(subscription);
    }

    @Override
    public PageResult<ReportSubscriptionDTO> queryPage(ReportSubscriptionQueryDTO dto) {
        LambdaQueryWrapper<ReportSubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getReportId() != null, ReportSubscription::getReportId, dto.getReportId())
               .eq(dto.getUserId() != null, ReportSubscription::getUserId, dto.getUserId())
               .like(dto.getSubscriptionName() != null, ReportSubscription::getSubscriptionName, dto.getSubscriptionName())
               .eq(dto.getStatus() != null, ReportSubscription::getStatus, dto.getStatus())
               .orderByDesc(ReportSubscription::getCreateTime);
        
        IPage<ReportSubscription> page = page(new Page<>(dto.getPage(), dto.getSize()), wrapper);
        return new PageResult<>(
            page.getCurrent(),
            page.getSize(),
            page.getTotal(),
            page.getRecords().stream().map(this::toDTO).toList()
        );
    }

    @Override
    public void pause(Long id) {
        ReportSubscription subscription = super.getById(id);
        if (subscription == null) {
            throw new BusinessException("报表订阅不存在");
        }
        subscription.setStatus(0);
        updateById(subscription);
    }

    @Override
    public void resume(Long id) {
        ReportSubscription subscription = super.getById(id);
        if (subscription == null) {
            throw new BusinessException("报表订阅不存在");
        }
        subscription.setStatus(1);
        updateById(subscription);
    }

    @Override
    public void executeNow(Long id) {
        ReportSubscription subscription = super.getById(id);
        if (subscription == null) {
            throw new BusinessException("报表订阅不存在");
        }
        subscription.setLastRunTime(LocalDateTime.now());
        updateById(subscription);
    }

    private ReportSubscriptionDTO toDTO(ReportSubscription subscription) {
        ReportSubscriptionDTO dto = new ReportSubscriptionDTO();
        BeanUtils.copyProperties(subscription, dto);
        return dto;
    }
}
