package com.qoobot.qooerp.notify.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.notify.dto.DndSettingDTO;
import com.qoobot.qooerp.notify.dto.SubscriptionDTO;

import java.util.List;
import java.util.Map;

/**
 * 订阅服务接口
 */
public interface SubscriptionService {

    /**
     * 订阅通知
     */
    void subscribe(SubscriptionDTO dto);

    /**
     * 取消订阅
     */
    void unsubscribe(Long userId, String channelType);

    /**
     * 获取用户订阅列表
     */
    List<Map<String, Object>> getUserSubscriptions(Long userId);

    /**
     * 设置免打扰
     */
    void setDND(DndSettingDTO dto);

    /**
     * 判断是否在免打扰时间内
     */
    boolean isDndTime(Long userId);
}
