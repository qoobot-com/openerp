package com.qoobot.qooerp.hr.lifecycle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.lifecycle.domain.HrOnboarding;
import java.util.List;

/**
 * 入职流程服务接口
 */
public interface IHrOnboardingService extends IService<HrOnboarding> {

    /**
     * 创建入职流程
     */
    HrOnboarding createOnboarding(HrOnboarding onboarding);

    /**
     * 更新入职流程
     */
    void updateOnboarding(HrOnboarding onboarding);

    /**
     * 删除入职流程
     */
    void deleteOnboarding(Long id);

    /**
     * 发送Offer
     */
    void sendOffer(Long id);

    /**
     * 接受Offer
     */
    void acceptOffer(Long id);

    /**
     * 开始背景调查
     */
    void startBgCheck(Long id);

    /**
     * 完成背景调查
     */
    void completeBgCheck(Long id, String report, Boolean passed);

    /**
     * 完成入职
     */
    void completeOnboarding(Long id);

    /**
     * 取消入职
     */
    void cancelOnboarding(Long id);

    /**
     * 按员工ID查询入职流程
     */
    HrOnboarding getByEmployeeId(Long employeeId);

    /**
     * 查询待入职列表
     */
    List<HrOnboarding> getPendingOnboardings();
}
