package com.qoobot.qooerp.hr.schedule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.schedule.domain.HrScheduleRule;
import com.qoobot.qooerp.hr.schedule.mapper.HrScheduleRuleMapper;
import com.qoobot.qooerp.hr.schedule.service.IHrScheduleRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 排班规则服务实现
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrScheduleRuleServiceImpl extends ServiceImpl<HrScheduleRuleMapper, HrScheduleRule>
        implements IHrScheduleRuleService {

    @Override
    public Long createRule(HrScheduleRule rule) {
        log.info("创建排班规则: {}", rule.getRuleName());
        this.save(rule);
        return rule.getId();
    }

    @Override
    public Boolean updateRule(HrScheduleRule rule) {
        log.info("更新排班规则: {}", rule.getId());
        return this.updateById(rule);
    }

    @Override
    public Boolean deleteRule(Long ruleId) {
        log.info("删除排班规则: {}", ruleId);
        return this.removeById(ruleId);
    }

    @Override
    public List<HrScheduleRule> getRulesByDept(Long deptId) {
        LambdaQueryWrapper<HrScheduleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.isNull(HrScheduleRule::getDeptId)
                .or()
                .eq(HrScheduleRule::getDeptId, deptId));
        wrapper.eq(HrScheduleRule::getStatus, 1);
        wrapper.orderByDesc(HrScheduleRule::getCreateTime);
        return this.list(wrapper);
    }
}
