package com.qoobot.qooerp.finance.period.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.finance.period.domain.FinancePeriod;

import java.util.List;

/**
 * 会计期间服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IFinancePeriodService extends IService<FinancePeriod> {

    /**
     * 创建会计期间
     *
     * @param period 会计期间信息
     * @return 创建后的会计期间
     */
    FinancePeriod createPeriod(FinancePeriod period);

    /**
     * 打开期间
     *
     * @param periodId 期间ID
     */
    void openPeriod(Long periodId);

    /**
     * 结账
     *
     * @param periodId 期间ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     */
    void closePeriod(Long periodId, Long operatorId, String operatorName);

    /**
     * 反结账
     *
     * @param periodId 期间ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     */
    void reopenPeriod(Long periodId, Long operatorId, String operatorName);

    /**
     * 查询年度所有期间
     *
     * @param fiscalYear 会计年度
     * @return 期间列表
     */
    List<FinancePeriod> getPeriodsByYear(Integer fiscalYear);

    /**
     * 查询当前期间
     *
     * @return 当前期间
     */
    FinancePeriod getCurrentPeriod();

    /**
     * 查询指定日期所属期间
     *
     * @param date 日期
     * @return 期间
     */
    FinancePeriod getPeriodByDate(java.time.LocalDate date);

    /**
     * 检查期间是否允许录入凭证
     *
     * @param periodId 期间ID
     * @return 是否允许
     */
    boolean isAllowEntry(Long periodId);
}
