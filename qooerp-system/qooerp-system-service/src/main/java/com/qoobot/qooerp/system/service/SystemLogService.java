package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemLogDTO;
import com.qoobot.qooerp.system.entity.SystemLog;
import com.qoobot.qooerp.system.vo.SystemLogVO;

import java.util.List;

/**
 * 操作日志服务接口
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public interface SystemLogService extends IService<SystemLog> {

    /**
     * 创建日志（用于切面直接保存实体）
     *
     * @param systemLog 日志实体
     */
    void createLog(SystemLog systemLog);

    /**
     * 保存日志
     *
     * @param dto 日志DTO
     * @return 日志ID
     */
    Long saveLog(SystemLogDTO dto);

    /**
     * 异步保存日志
     *
     * @param dto 日志DTO
     */
    void saveLogAsync(SystemLogDTO dto);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return 日志VO
     */
    SystemLogVO getLog(Long id);

    /**
     * 分页查询日志
     *
     * @param current   当前页
     * @param size      每页大小
     * @param logType   日志类型
     * @param module    模块名称
     * @param username  用户名
     * @param ip        IP地址
     * @param status    状态
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 分页结果
     */
    Page<SystemLogVO> pageLog(Long current, Long size, String logType, String module, String username,
                              String ip, Integer status, String startTime, String endTime);

    /**
     * 日志统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 统计结果
     */
    Object statistics(String startDate, String endDate);

    /**
     * 批量删除日志
     *
     * @param ids 日志ID列表
     * @return 是否成功
     */
    boolean batchDeleteLog(List<Long> ids);

    /**
     * 清理过期日志
     *
     * @param days 保留天数
     * @return 删除数量
     */
    Integer cleanExpireLogs(Integer days);
}
