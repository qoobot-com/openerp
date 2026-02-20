package com.qoobot.qooerp.scheduler.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleNotify;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleNotifyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报警统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyStatisticsServiceImpl {

    private final ScheduleNotifyMapper scheduleNotifyMapper;

    /**
     * 报警次数统计
     */
    public Map<String, Long> getNotifyCountStatistics() {
        LambdaQueryWrapper<ScheduleNotify> wrapper = new LambdaQueryWrapper<>();
        List<ScheduleNotify> notifies = scheduleNotifyMapper.selectList(wrapper);

        Map<String, Long> statistics = notifies.stream()
                .collect(Collectors.groupingBy(
                        ScheduleNotify::getStatus,
                        Collectors.counting()
                ));

        return statistics;
    }

    /**
     * 报警类型统计
     */
    public Map<String, Long> getNotifyTypeStatistics() {
        LambdaQueryWrapper<ScheduleNotify> wrapper = new LambdaQueryWrapper<>();
        List<ScheduleNotify> notifies = scheduleNotifyMapper.selectList(wrapper);

        Map<String, Long> statistics = notifies.stream()
                .collect(Collectors.groupingBy(
                        ScheduleNotify::getNotifyType,
                        Collectors.counting()
                ));

        return statistics;
    }

    /**
     * 报警级别统计
     */
    public Map<String, Long> getNotifyLevelStatistics() {
        LambdaQueryWrapper<ScheduleNotify> wrapper = new LambdaQueryWrapper<>();
        List<ScheduleNotify> notifies = scheduleNotifyMapper.selectList(wrapper);

        Map<String, Long> statistics = notifies.stream()
                .collect(Collectors.groupingBy(
                        ScheduleNotify::getNotifyLevel,
                        Collectors.counting()
                ));

        return statistics;
    }

    /**
     * 今日报警统计
     */
    public Map<String, Long> getTodayNotifyStatistics() {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        LambdaQueryWrapper<ScheduleNotify> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ScheduleNotify::getNotifyTime, todayStart);
        List<ScheduleNotify> notifies = scheduleNotifyMapper.selectList(wrapper);

        Map<String, Long> statistics = notifies.stream()
                .collect(Collectors.groupingBy(
                        ScheduleNotify::getStatus,
                        Collectors.counting()
                ));

        return statistics;
    }

    /**
     * 获取未处理报警数量
     */
    public Long getPendingNotifyCount() {
        LambdaQueryWrapper<ScheduleNotify> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleNotify::getStatus, "PENDING");
        return scheduleNotifyMapper.selectCount(wrapper);
    }

    /**
     * 获取待发送报警数量
     */
    public Long getUnsentNotifyCount() {
        LambdaQueryWrapper<ScheduleNotify> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ScheduleNotify::getStatus, "PENDING", "FAILED");
        return scheduleNotifyMapper.selectCount(wrapper);
    }
}
