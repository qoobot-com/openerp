package com.qoobot.qooerp.notify.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.notify.dto.NotifyRecordDTO;
import com.qoobot.qooerp.notify.dto.NotifyRecordQueryDTO;
import com.qoobot.qooerp.notify.dto.StatisticsDTO;

import java.util.Map;

/**
 * 记录服务接口
 */
public interface RecordService {

    /**
     * 记录分页查询
     */
    Page<NotifyRecordDTO> page(NotifyRecordQueryDTO dto);

    /**
     * 获取记录详情
     */
    NotifyRecordDTO get(Long id);

    /**
     * 重试发送
     */
    void retry(Long id);

    /**
     * 统计分析
     */
    Map<String, Object> statistics(StatisticsDTO dto);
}
