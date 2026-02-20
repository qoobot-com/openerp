package com.qoobot.qooerp.finance.period.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.finance.period.domain.FinancePeriod;
import com.qoobot.qooerp.finance.period.mapper.FinancePeriodMapper;
import com.qoobot.qooerp.finance.period.service.IFinancePeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会计期间服务实现类
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinancePeriodServiceImpl extends ServiceImpl<FinancePeriodMapper, FinancePeriod> implements IFinancePeriodService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancePeriod createPeriod(FinancePeriod period) {
        // 校验参数
        if (period.getFiscalYear() == null || period.getPeriodNo() == null) {
            throw new BusinessException("会计年度和期间不能为空");
        }
        if (period.getStartDate() == null || period.getEndDate() == null) {
            throw new BusinessException("开始日期和结束日期不能为空");
        }
        if (period.getStartDate().isAfter(period.getEndDate())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }

        // 检查期间是否已存在
        LambdaQueryWrapper<FinancePeriod> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinancePeriod::getFiscalYear, period.getFiscalYear())
                .eq(FinancePeriod::getPeriodNo, period.getPeriodNo());
        if (count(wrapper) > 0) {
            throw new BusinessException("该会计年度期间已存在");
        }

        // 初始化字段
        period.setStatus(0); // 未开始
        period.setAllowEntry(0); // 不允许录入
        period.setReopenCount(0);
        period.setPeriodCode(generatePeriodCode(period.getFiscalYear(), period.getPeriodNo()));

        save(period);
        log.info("创建会计期间成功，期间: {}", period.getPeriodCode());

        return period;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void openPeriod(Long periodId) {
        FinancePeriod period = getById(periodId);
        if (period == null) {
            throw new BusinessException("会计期间不存在");
        }

        if (period.getStatus() == 2) {
            throw new BusinessException("已结账的期间无法重新打开，请使用反结账功能");
        }

        period.setStatus(1); // 进行中
        period.setAllowEntry(1); // 允许录入
        updateById(period);

        log.info("打开会计期间成功，期间: {}", period.getPeriodCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closePeriod(Long periodId, Long operatorId, String operatorName) {
        FinancePeriod period = getById(periodId);
        if (period == null) {
            throw new BusinessException("会计期间不存在");
        }

        if (period.getStatus() != 1) {
            throw new BusinessException("只有进行中的期间才能结账");
        }

        period.setStatus(2); // 已结账
        period.setAllowEntry(0); // 不允许录入
        period.setCloserId(String.valueOf(operatorId));
        period.setCloserName(operatorName);
        period.setCloseTime(LocalDateTime.now());

        updateById(period);
        log.info("结账成功，期间: {}, 结账人: {}", period.getPeriodCode(), operatorName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reopenPeriod(Long periodId, Long operatorId, String operatorName) {
        FinancePeriod period = getById(periodId);
        if (period == null) {
            throw new BusinessException("会计期间不存在");
        }

        if (period.getStatus() != 2) {
            throw new BusinessException("只有已结账的期间才能反结账");
        }

        // 检查反结账次数限制（例如最多3次）
        if (period.getReopenCount() != null && period.getReopenCount() >= 3) {
            throw new BusinessException("反结账次数已达上限（最多3次）");
        }

        period.setStatus(1); // 进行中
        period.setAllowEntry(1); // 允许录入
        period.setReopenCount(period.getReopenCount() == null ? 1 : period.getReopenCount() + 1);

        updateById(period);
        log.info("反结账成功，期间: {}, 操作人: {}", period.getPeriodCode(), operatorName);
    }

    @Override
    public List<FinancePeriod> getPeriodsByYear(Integer fiscalYear) {
        LambdaQueryWrapper<FinancePeriod> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinancePeriod::getFiscalYear, fiscalYear)
                .orderByAsc(FinancePeriod::getPeriodNo);
        return list(wrapper);
    }

    @Override
    public FinancePeriod getCurrentPeriod() {
        LambdaQueryWrapper<FinancePeriod> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinancePeriod::getStatus, 1) // 进行中
                .last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public FinancePeriod getPeriodByDate(LocalDate date) {
        LambdaQueryWrapper<FinancePeriod> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(FinancePeriod::getStartDate, date)
                .ge(FinancePeriod::getEndDate, date)
                .last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public boolean isAllowEntry(Long periodId) {
        FinancePeriod period = getById(periodId);
        return period != null && period.getAllowEntry() != null && period.getAllowEntry() == 1;
    }

    /**
     * 生成期间编码
     */
    private String generatePeriodCode(Integer fiscalYear, Integer periodNo) {
        return String.format("%04d-%02d", fiscalYear, periodNo);
    }
}
