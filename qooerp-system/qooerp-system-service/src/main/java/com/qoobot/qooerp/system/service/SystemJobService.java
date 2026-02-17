package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemJobDTO;
import com.qoobot.qooerp.system.entity.SystemJob;
import com.qoobot.qooerp.system.vo.SystemJobVO;

/**
 * 定时任务服务接口
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public interface SystemJobService extends IService<SystemJob> {

    /**
     * 创建定时任务
     *
     * @param dto 任务DTO
     * @return 任务ID
     */
    Long createJob(SystemJobDTO dto);

    /**
     * 更新定时任务
     *
     * @param id  任务ID
     * @param dto 任务DTO
     * @return 是否成功
     */
    boolean updateJob(Long id, SystemJobDTO dto);

    /**
     * 删除定时任务
     *
     * @param id 任务ID
     * @return 是否成功
     */
    boolean deleteJob(Long id);

    /**
     * 获取任务详情
     *
     * @param id 任务ID
     * @return 任务VO
     */
    SystemJobVO getJob(Long id);

    /**
     * 分页查询定时任务
     *
     * @param current  当前页
     * @param size     每页大小
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @param status   状态
     * @return 分页结果
     */
    Page<SystemJobVO> pageJob(Long current, Long size, String jobName, String jobGroup, Integer status);

    /**
     * 启用任务
     *
     * @param id 任务ID
     * @return 是否成功
     */
    boolean startJob(Long id);

    /**
     * 停用任务
     *
     * @param id 任务ID
     * @return 是否成功
     */
    boolean stopJob(Long id);

    /**
     * 立即执行任务
     *
     * @param id 任务ID
     * @return 执行结果
     */
    Object executeJob(Long id);

    /**
     * 初始化所有任务
     */
    void initJobs();
}
