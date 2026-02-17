package com.qoobot.qooerp.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.auth.entity.AuthDevice;
import com.qoobot.qooerp.auth.mapper.AuthDeviceMapper;
import com.qoobot.qooerp.auth.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final AuthDeviceMapper authDeviceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerDevice(Long userId, String deviceInfo) {
        // 解析设备信息
        String[] parts = deviceInfo.split("\\|");
        String deviceType = parts.length > 0 ? parts[0] : "PC";
        String browser = parts.length > 1 ? parts[1] : "未知";
        String os = parts.length > 2 ? parts[2] : "未知";
        String deviceFingerprint = parts.length > 3 ? parts[3] : java.util.UUID.randomUUID().toString();

        // 检查是否已存在相同设备
        LambdaQueryWrapper<AuthDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthDevice::getUserId, userId);
        queryWrapper.eq(AuthDevice::getDeviceFingerprint, deviceFingerprint);
        queryWrapper.eq(AuthDevice::getDeleted, 0);
        AuthDevice existingDevice = authDeviceMapper.selectOne(queryWrapper);

        if (existingDevice != null) {
            // 更新活跃时间
            updateDeviceActiveTime(existingDevice.getId());
            return existingDevice.getId();
        }

        // 创建新设备
        AuthDevice device = new AuthDevice();
        device.setUserId(userId);
        device.setDeviceName(generateDeviceName(deviceType));
        device.setDeviceType(deviceType);
        device.setDeviceFingerprint(deviceFingerprint);
        device.setBrowser(browser);
        device.setOs(os);
        device.setRemembered(false);
        device.setActive(true);
        device.setLastActiveTime(LocalDateTime.now());
        device.setCreateTime(LocalDateTime.now());
        authDeviceMapper.insert(device);

        log.info("设备注册成功，userId: {}, deviceId: {}", userId, device.getId());
        return device.getId();
    }

    @Override
    public boolean validateDevice(Long userId, Long deviceId) {
        if (deviceId == null) {
            return false;
        }

        AuthDevice device = authDeviceMapper.selectById(deviceId);
        return device != null
                && device.getUserId().equals(userId)
                && device.getActive()
                && device.getDeleted() == 0;
    }

    @Override
    public List<AuthDevice> getUserDevices(Long userId) {
        LambdaQueryWrapper<AuthDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthDevice::getUserId, userId);
        queryWrapper.eq(AuthDevice::getDeleted, 0);
        queryWrapper.orderByDesc(AuthDevice::getLastActiveTime);
        return authDeviceMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeDevice(Long userId, Long deviceId) {
        AuthDevice device = authDeviceMapper.selectById(deviceId);

        if (device == null || !device.getUserId().equals(userId)) {
            return false;
        }

        device.setDeleted(1);
        authDeviceMapper.updateById(device);

        log.info("设备删除成功，userId: {}, deviceId: {}", userId, deviceId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearAllDevices(Long userId) {
        LambdaQueryWrapper<AuthDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthDevice::getUserId, userId);
        queryWrapper.eq(AuthDevice::getDeleted, 0);
        List<AuthDevice> devices = authDeviceMapper.selectList(queryWrapper);

        for (AuthDevice device : devices) {
            device.setDeleted(1);
            authDeviceMapper.updateById(device);
        }

        log.info("清除用户所有设备成功，userId: {}, deviceCount: {}", userId, devices.size());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeviceActiveTime(Long deviceId) {
        AuthDevice device = authDeviceMapper.selectById(deviceId);
        if (device != null) {
            device.setLastActiveTime(LocalDateTime.now());
            authDeviceMapper.updateById(device);
        }
    }

    /**
     * 生成设备名称
     */
    private String generateDeviceName(String deviceType) {
        switch (deviceType) {
            case "PC":
                return "电脑浏览器";
            case "MOBILE":
                return "手机浏览器";
            case "TABLET":
                return "平板电脑";
            case "APP":
                return "移动应用";
            default:
                return "未知设备";
        }
    }
}
