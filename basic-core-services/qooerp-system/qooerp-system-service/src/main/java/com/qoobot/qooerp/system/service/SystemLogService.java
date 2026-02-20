package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemLogDTO;
import com.qoobot.qooerp.system.dto.SystemLogQueryDTO;
import com.qoobot.qooerp.system.entity.SystemLog;

import java.util.Map;

/**
 * 操作日志服务接口
 */
public interface SystemLogService extends IService<SystemLog> {

    /**
     * 获取日志
     */
    SystemLogDTO getLog(Long id);

    /**
     * 分页查询日志
     */
    IPage<SystemLogDTO> pageLog(Integer current, Integer size, SystemLogQueryDTO queryDTO);

    /**
     * 日志统计
     */
    Map<String, Object> getStatistics(SystemLogQueryDTO queryDTO);

    /**
     * 删除日志
     */
    void deleteLog(Long id);

    /**
     * 批量删除日志
     */
    void batchDeleteLog(String ids);

    /**
     * 记录日志
     */
    void saveLog(SystemLog log);
}
