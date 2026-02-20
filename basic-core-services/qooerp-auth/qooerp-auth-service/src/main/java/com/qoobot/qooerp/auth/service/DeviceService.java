package com.qoobot.qooerp.auth.service;

import com.qoobot.qooerp.auth.entity.AuthDevice;
import java.util.List;

/**
 * 设备管理服务接口
 */
public interface DeviceService {

    /**
     * 注册设备
     *
     * @param userId 用户ID
     * @param deviceInfo 设备信息
     * @return 设备ID
     */
    Long registerDevice(Long userId, String deviceInfo);

    /**
     * 验证设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否有效
     */
    boolean validateDevice(Long userId, Long deviceId);

    /**
     * 获取用户设备列表
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    List<AuthDevice> getUserDevices(Long userId);

    /**
     * 删除设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean removeDevice(Long userId, Long deviceId);

    /**
     * 清除用户所有设备（强制下线）
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearAllDevices(Long userId);

    /**
     * 更新设备活跃时间
     *
     * @param deviceId 设备ID
     */
    void updateDeviceActiveTime(Long deviceId);
}
