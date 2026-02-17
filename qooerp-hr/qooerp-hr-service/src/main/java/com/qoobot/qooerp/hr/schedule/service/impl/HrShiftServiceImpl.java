package com.qoobot.qooerp.hr.schedule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.schedule.domain.HrShift;
import com.qoobot.qooerp.hr.schedule.mapper.HrShiftMapper;
import com.qoobot.qooerp.hr.schedule.service.IHrShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 班次服务实现
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrShiftServiceImpl extends ServiceImpl<HrShiftMapper, HrShift> implements IHrShiftService {

    @Override
    public Long createShift(HrShift shift) {
        log.info("创建班次: {}", shift.getShiftName());
        this.save(shift);
        return shift.getId();
    }

    @Override
    public Boolean updateShift(HrShift shift) {
        log.info("更新班次: {}", shift.getId());
        return this.updateById(shift);
    }

    @Override
    public Boolean deleteShift(Long shiftId) {
        log.info("删除班次: {}", shiftId);
        return this.removeById(shiftId);
    }

    @Override
    public List<HrShift> getActiveShifts() {
        LambdaQueryWrapper<HrShift> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrShift::getStatus, 1);
        wrapper.orderByAsc(HrShift::getSortOrder);
        return this.list(wrapper);
    }
}
