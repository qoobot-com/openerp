package com.qoobot.qooerp.scheduler.notify.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.scheduler.job.dto.ScheduleNotifyDTO;

/**
 * 任务报警 Service 接口
 */
public interface ScheduleNotifyService {

    /**
     * 发送报警通知
     */
    void sendNotify(Long jobId, String notifyType, String notifyLevel, String notifyContent);

    /**
     * 查询报警记录
     */
    Page<ScheduleNotifyDTO> queryNotifies(Long jobId, String status, Integer current, Integer size);

    /**
     * 处理报警
     */
    void handleNotify(Long notifyId, String handler, String handleRemark);
}
