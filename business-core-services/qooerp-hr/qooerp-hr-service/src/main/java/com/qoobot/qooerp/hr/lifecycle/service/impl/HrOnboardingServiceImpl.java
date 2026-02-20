package com.qoobot.qooerp.hr.lifecycle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.lifecycle.domain.HrOnboarding;
import com.qoobot.qooerp.hr.lifecycle.mapper.HrOnboardingMapper;
import com.qoobot.qooerp.hr.lifecycle.service.IHrOnboardingService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 入职流程服务实现
 */
@Service
public class HrOnboardingServiceImpl extends ServiceImpl<HrOnboardingMapper, HrOnboarding> implements IHrOnboardingService {

    @Override
    public HrOnboarding createOnboarding(HrOnboarding onboarding) {
        // 生成入职单号
        String onboardingNo = generateOnboardingNo();
        onboarding.setOnboardingNo(onboardingNo);
        onboarding.setStatus("DRAFT");
        onboarding.setOfferStatus("NOT_SENT");
        onboarding.setBgCheckStatus("NOT_REQUIRED");
        onboarding.setCreateTime(java.time.LocalDateTime.now());
        save(onboarding);
        return onboarding;
    }

    @Override
    public void updateOnboarding(HrOnboarding onboarding) {
        onboarding.setUpdateTime(java.time.LocalDateTime.now());
        updateById(onboarding);
    }

    @Override
    public void deleteOnboarding(Long id) {
        removeById(id);
    }

    @Override
    public void sendOffer(Long id) {
        HrOnboarding onboarding = getById(id);
        if (onboarding != null) {
            onboarding.setOfferStatus("SENT");
            onboarding.setOfferDate(LocalDate.now());
            onboarding.setStatus("PENDING");
            updateById(onboarding);
        }
    }

    @Override
    public void acceptOffer(Long id) {
        HrOnboarding onboarding = getById(id);
        if (onboarding != null) {
            onboarding.setOfferStatus("ACCEPTED");
            onboarding.setAcceptDate(LocalDate.now());
            updateById(onboarding);
        }
    }

    @Override
    public void startBgCheck(Long id) {
        HrOnboarding onboarding = getById(id);
        if (onboarding != null) {
            onboarding.setBgCheckStatus("PROCESSING");
            updateById(onboarding);
        }
    }

    @Override
    public void completeBgCheck(Long id, String report, Boolean passed) {
        HrOnboarding onboarding = getById(id);
        if (onboarding != null) {
            onboarding.setBgCheckStatus(passed ? "PASSED" : "FAILED");
            onboarding.setBgCheckReport(report);
            updateById(onboarding);
        }
    }

    @Override
    public void completeOnboarding(Long id) {
        HrOnboarding onboarding = getById(id);
        if (onboarding != null) {
            onboarding.setStatus("COMPLETED");
            onboarding.setUpdateTime(java.time.LocalDateTime.now());
            updateById(onboarding);
        }
    }

    @Override
    public void cancelOnboarding(Long id) {
        HrOnboarding onboarding = getById(id);
        if (onboarding != null) {
            onboarding.setStatus("CANCELLED");
            updateById(onboarding);
        }
    }

    @Override
    public HrOnboarding getByEmployeeId(Long employeeId) {
        return lambdaQuery().eq(HrOnboarding::getEmployeeId, employeeId).one();
    }

    @Override
    public java.util.List<HrOnboarding> getPendingOnboardings() {
        return lambdaQuery().in(HrOnboarding::getStatus, 
            java.util.Arrays.asList("PENDING", "PROCESSING")).list();
    }

    /**
     * 生成入职单号
     */
    private String generateOnboardingNo() {
        return "ONB" + System.currentTimeMillis();
    }
}
