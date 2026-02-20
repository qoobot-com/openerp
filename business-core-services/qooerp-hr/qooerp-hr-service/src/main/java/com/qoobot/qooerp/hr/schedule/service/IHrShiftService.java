package com.qoobot.qooerp.hr.schedule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.schedule.domain.HrShift;

/**
 * 班次服务接口
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
public interface IHrShiftService extends IService<HrShift> {

    /**
     * 创建班次
     *
     * @param shift 班次信息
     * @return 班次ID
     */
    Long createShift(HrShift shift);

    /**
     * 更新班次
     *
     * @param shift 班次信息
     * @return 是否成功
     */
    Boolean updateShift(HrShift shift);

    /**
     * 删除班次
     *
     * @param shiftId 班次ID
     * @return 是否成功
     */
    Boolean deleteShift(Long shiftId);

    /**
     * 获取启用的班次列表
     *
     * @return 班次列表
     */
    java.util.List<HrShift> getActiveShifts();
}
