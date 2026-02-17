package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.context.TenantContext;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.entity.ProductionSchedule;
import com.qoobot.qooerp.production.mapper.ProductionScheduleMapper;
import com.qoobot.qooerp.production.service.ProductionScheduleService;
import com.qoobot.qooerp.production.vo.ProductionScheduleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionScheduleServiceImpl extends ServiceImpl<ProductionScheduleMapper, ProductionSchedule> implements ProductionScheduleService {

    private final ProductionScheduleMapper productionScheduleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionSchedule(ProductionSchedule schedule) {
        LambdaQueryWrapper<ProductionSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionSchedule::getScheduleCode, schedule.getScheduleCode())
                .eq(ProductionSchedule::getTenantId, TenantContext.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("排程编码已存在");
        }

        schedule.setTenantId(TenantContext.getTenantId());
        this.save(schedule);
        log.info("创建生产排程成功: scheduleCode={}", schedule.getScheduleCode());
        return schedule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionSchedule(Long id, ProductionSchedule schedule) {
        ProductionSchedule exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException("生产排程不存在");
        }

        schedule.setId(id);
        schedule.setTenantId(TenantContext.getTenantId());
        this.updateById(schedule);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionSchedule(Long id) {
        ProductionSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException("生产排程不存在");
        }
        this.removeById(id);
        return true;
    }

    @Override
    public ProductionSchedule getProductionScheduleById(Long id) {
        ProductionSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException("生产排程不存在");
        }
        return schedule;
    }

    @Override
    public IPage<ProductionScheduleVO> queryProductionSchedulePage(Integer current, Integer size, String scheduleCode, String status, Long equipmentId) {
        Page<ProductionSchedule> page = new Page<>(current, size);
        IPage<ProductionSchedule> schedulePage = productionScheduleMapper.selectPageByCondition(
                page, scheduleCode, status, equipmentId, TenantContext.getTenantId()
        );

        return schedulePage.convert(schedule -> {
            ProductionScheduleVO vo = BeanUtil.copyProperties(schedule, ProductionScheduleVO.class);
            vo.setStatusDesc(getStatusDesc(schedule.getStatus()));
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startSchedule(Long id) {
        ProductionSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException("生产排程不存在");
        }
        if (!"pending".equals(schedule.getStatus())) {
            throw new BusinessException("只有待开始的排程才能开始");
        }

        schedule.setStatus("in_progress");
        schedule.setActualStartTime(LocalDateTime.now());
        this.updateById(schedule);
        log.info("开始生产排程: scheduleCode={}", schedule.getScheduleCode());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeSchedule(Long id) {
        ProductionSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException("生产排程不存在");
        }
        if (!"in_progress".equals(schedule.getStatus())) {
            throw new BusinessException("只有进行中的排程才能完成");
        }

        schedule.setStatus("completed");
        schedule.setActualEndTime(LocalDateTime.now());
        this.updateById(schedule);
        log.info("完成生产排程: scheduleCode={}", schedule.getScheduleCode());
        return true;
    }

    private String getStatusDesc(String status) {
        return switch (status) {
            case "pending" -> "待开始";
            case "in_progress" -> "进行中";
            case "completed" -> "已完成";
            case "cancelled" -> "已取消";
            default -> status;
        };
    }
}
