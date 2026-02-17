package com.qoobot.qooerp.notify.service;

import com.qoobot.qooerp.notify.dto.NotifyRecordDTO;
import com.qoobot.qooerp.notify.dto.NotifySendDTO;
import com.qoobot.qooerp.notify.entity.NotifyRecord;

/**
 * 通知服务接口
 */
public interface NotifyService {

    /**
     * 发送通知
     */
    NotifyRecordDTO send(NotifySendDTO dto);

    /**
     * 批量发送通知
     */
    void sendBatch(NotifySendDTO dto, String[] receivers);

    /**
     * 重新发送通知
     */
    void resend(NotifyRecord record);
}
