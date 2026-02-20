package com.qoobot.qooerp.scheduler.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleNotify;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleNotifyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl {

    private final ScheduleNotifyMapper scheduleNotifyMapper;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired(required = false)
    private ThreadPoolTaskExecutor notifyExecutor;

    /**
     * 发送邮件通知
     */
    public void sendEmail(String to, String subject, String content) {
        if (mailSender == null) {
            log.warn("邮件发送器未配置,跳过邮件发送: to={}, subject={}", to, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("邮件发送成功: to={}", to);
        } catch (Exception e) {
            log.error("邮件发送失败: to={}", to, e);
        }
    }

    /**
     * 异步发送邮件通知
     */
    public void sendEmailAsync(String to, String subject, String content) {
        if (notifyExecutor != null) {
            notifyExecutor.execute(() -> sendEmail(to, subject, content));
        } else {
            sendEmail(to, subject, content);
        }
    }

    /**
     * 发送短信通知(框架)
     */
    public void sendSms(String phone, String content) {
        log.info("发送短信通知: phone={}, content={}", phone, content);
        // TODO: 集成短信服务
    }

    /**
     * 发送站内信通知(框架)
     */
    public void sendStationMessage(Long userId, String title, String content) {
        log.info("发送站内信通知: userId={}, title={}", userId, title);
        // TODO: 集成站内信服务
    }

    /**
     * 处理待发送的报警通知
     */
    public void processPendingNotifies() {
        LambdaQueryWrapper<ScheduleNotify> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScheduleNotify::getStatus, "PENDING")
                   .last("LIMIT 100");

        List<ScheduleNotify> notifies = scheduleNotifyMapper.selectList(queryWrapper);
        
        for (ScheduleNotify notify : notifies) {
            try {
                processNotify(notify);
            } catch (Exception e) {
                log.error("处理报警通知失败: notifyId={}", notify.getId(), e);
            }
        }
    }

    /**
     * 处理单个报警通知
     */
    private void processNotify(ScheduleNotify notify) {
        try {
            // 根据报警类型和级别发送通知
            String notifyLevel = notify.getNotifyLevel();
            
            if ("HIGH".equals(notifyLevel) || "MEDIUM".equals(notifyLevel)) {
                // 高中级报警发送邮件
                String subject = "任务调度报警通知 - " + notify.getNotifyType();
                sendEmailAsync("admin@example.com", subject, notify.getNotifyContent());
            }
            
            if ("HIGH".equals(notifyLevel)) {
                // 高级报警发送短信
                sendSms("13800138000", notify.getNotifyContent());
            }
            
            // 更新通知状态
            LambdaUpdateWrapper<ScheduleNotify> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ScheduleNotify::getId, notify.getId())
                        .set(ScheduleNotify::getStatus, "SENT")
                        .set(ScheduleNotify::getHandleTime, LocalDateTime.now());
            scheduleNotifyMapper.update(null, updateWrapper);
            
            log.info("报警通知处理完成: notifyId={}", notify.getId());
            
        } catch (Exception e) {
            log.error("处理报警通知失败: notifyId={}", notify.getId(), e);
            
            // 更新通知状态为失败
            LambdaUpdateWrapper<ScheduleNotify> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ScheduleNotify::getId, notify.getId())
                        .set(ScheduleNotify::getStatus, "FAILED")
                        .set(ScheduleNotify::getHandleTime, LocalDateTime.now());
            scheduleNotifyMapper.update(null, updateWrapper);
        }
    }
}
