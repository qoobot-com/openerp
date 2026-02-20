package com.qoobot.qooerp.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.notify.dto.DndSettingDTO;
import com.qoobot.qooerp.notify.dto.SubscriptionDTO;
import com.qoobot.qooerp.notify.entity.NotifySubscription;
import com.qoobot.qooerp.notify.mapper.NotifySubscriptionMapper;
import com.qoobot.qooerp.notify.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订阅服务实现
 */
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final NotifySubscriptionMapper subscriptionMapper;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void subscribe(SubscriptionDTO dto) {
        LambdaQueryWrapper<NotifySubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifySubscription::getTenantId, TenantContextHolder.getTenantId());
        wrapper.eq(NotifySubscription::getUserId, dto.getUserId());
        wrapper.eq(NotifySubscription::getChannelType, dto.getChannelType());

        NotifySubscription subscription = subscriptionMapper.selectOne(wrapper);
        if (subscription != null) {
            subscription.setReceiver(dto.getReceiver());
            subscription.setSubscribed(1);
            subscription.setUpdateTime(LocalDateTime.now());
            subscriptionMapper.updateById(subscription);
        } else {
            subscription = new NotifySubscription();
            subscription.setTenantId(TenantContextHolder.getTenantId());
            subscription.setUserId(dto.getUserId());
            subscription.setChannelType(dto.getChannelType());
            subscription.setReceiver(dto.getReceiver());
            subscription.setSubscribed(1);
            subscription.setCreateTime(LocalDateTime.now());
            subscription.setUpdateTime(LocalDateTime.now());
            subscriptionMapper.insert(subscription);
        }
    }

    @Override
    public void unsubscribe(Long userId, String channelType) {
        LambdaQueryWrapper<NotifySubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifySubscription::getTenantId, TenantContextHolder.getTenantId());
        wrapper.eq(NotifySubscription::getUserId, userId);
        wrapper.eq(NotifySubscription::getChannelType, channelType);

        subscriptionMapper.delete(wrapper);
    }

    @Override
    public List<Map<String, Object>> getUserSubscriptions(Long userId) {
        LambdaQueryWrapper<NotifySubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifySubscription::getTenantId, TenantContextHolder.getTenantId());
        wrapper.eq(NotifySubscription::getUserId, userId);

        List<NotifySubscription> subscriptions = subscriptionMapper.selectList(wrapper);
        return subscriptions.stream().map(sub -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sub.getId());
            map.put("userId", sub.getUserId());
            map.put("channelType", sub.getChannelType());
            map.put("receiver", sub.getReceiver());
            map.put("status", sub.getSubscribed());
            return map;
        }).toList();
    }

    @Override
    public void setDND(DndSettingDTO dto) {
        // TODO: 实现免打扰设置逻辑，将设置保存到数据库
    }

    @Override
    public boolean isDndTime(Long userId) {
        // TODO: 查询用户免打扰设置，判断当前时间是否在免打扰时间段内
        return false;
    }
}
