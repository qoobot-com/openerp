package com.qoobot.qooerp.scheduler.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleNotify;
import com.qoobot.qooerp.scheduler.job.dto.ScheduleNotifyDTO;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleNotifyMapper;
import com.qoobot.qooerp.scheduler.notify.service.ScheduleNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务报警 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleNotifyServiceImpl implements ScheduleNotifyService {

    private final ScheduleNotifyMapper notifyMapper;

    @Override
    public void sendNotify(Long jobId, String notifyType, String notifyLevel, String notifyContent) {
        log.info("发送报警通知, jobId: {}, notifyType: {}, notifyLevel: {}", jobId, notifyType, notifyLevel);

        ScheduleNotify notify = new ScheduleNotify();
        notify.setJobId(jobId);
        notify.setNotifyType(notifyType);
        notify.setNotifyLevel(notifyLevel);
        notify.setNotifyContent(notifyContent);
        notify.setNotifyTime(LocalDateTime.now());
        notify.setStatus("PENDING");
        notify.setTenantId(1L); // TODO: 从上下文获取租户ID

        notifyMapper.insert(notify);

        // TODO: 发送报警通知（邮件、短信、站内信）
    }

    @Override
    public Page<ScheduleNotifyDTO> queryNotifies(Long jobId, String status, Integer current, Integer size) {
        log.info("查询报警记录, jobId: {}, status: {}", jobId, status);

        Page<ScheduleNotify> page = new Page<>(current, size);
        LambdaQueryWrapper<ScheduleNotify> wrapper = new LambdaQueryWrapper<>();

        if (jobId != null) {
            wrapper.eq(ScheduleNotify::getJobId, jobId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ScheduleNotify::getStatus, status);
        }

        wrapper.orderByDesc(ScheduleNotify::getNotifyTime);
        Page<ScheduleNotify> resultPage = notifyMapper.selectPage(page, wrapper);

        Page<ScheduleNotifyDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<ScheduleNotifyDTO> dtoList = resultPage.getRecords().stream()
                .map(notify -> {
                    ScheduleNotifyDTO dto = new ScheduleNotifyDTO();
                    BeanUtils.copyProperties(notify, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public void handleNotify(Long notifyId, String handler, String handleRemark) {
        log.info("处理报警, notifyId: {}, handler: {}", notifyId, handler);

        ScheduleNotify notify = notifyMapper.selectById(notifyId);
        if (notify == null) {
            throw new RuntimeException("报警记录不存在");
        }

        notify.setStatus("HANDLED");
        notify.setHandler(handler);
        notify.setHandleTime(LocalDateTime.now());
        notify.setHandleRemark(handleRemark);

        notifyMapper.updateById(notify);
    }
}
