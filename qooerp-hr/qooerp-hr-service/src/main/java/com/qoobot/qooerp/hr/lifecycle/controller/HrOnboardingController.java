package com.qoobot.qooerp.hr.lifecycle.controller;

import com.qoobot.qooerp.common.core.domain.Result;
import com.qoobot.qooerp.hr.lifecycle.domain.HrOnboarding;
import com.qoobot.qooerp.hr.lifecycle.service.IHrOnboardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 入职流程控制器
 */
@RestController
@RequestMapping("/hr/onboarding")
public class HrOnboardingController {

    @Autowired
    private IHrOnboardingService onboardingService;

    /**
     * 创建入职流程
     */
    @PostMapping("/create")
    public Result<HrOnboarding> create(@RequestBody HrOnboarding onboarding) {
        return Result.success(onboardingService.createOnboarding(onboarding));
    }

    /**
     * 更新入职流程
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody HrOnboarding onboarding) {
        onboardingService.updateOnboarding(onboarding);
        return Result.success();
    }

    /**
     * 删除入职流程
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        onboardingService.deleteOnboarding(id);
        return Result.success();
    }

    /**
     * 发送Offer
     */
    @PostMapping("/sendOffer/{id}")
    public Result<Void> sendOffer(@PathVariable Long id) {
        onboardingService.sendOffer(id);
        return Result.success();
    }

    /**
     * 接受Offer
     */
    @PostMapping("/acceptOffer/{id}")
    public Result<Void> acceptOffer(@PathVariable Long id) {
        onboardingService.acceptOffer(id);
        return Result.success();
    }

    /**
     * 开始背景调查
     */
    @PostMapping("/startBgCheck/{id}")
    public Result<Void> startBgCheck(@PathVariable Long id) {
        onboardingService.startBgCheck(id);
        return Result.success();
    }

    /**
     * 完成背景调查
     */
    @PostMapping("/completeBgCheck/{id}")
    public Result<Void> completeBgCheck(@PathVariable Long id, @RequestParam String report, @RequestParam Boolean passed) {
        onboardingService.completeBgCheck(id, report, passed);
        return Result.success();
    }

    /**
     * 完成入职
     */
    @PostMapping("/completeOnboarding/{id}")
    public Result<Void> completeOnboarding(@PathVariable Long id) {
        onboardingService.completeOnboarding(id);
        return Result.success();
    }

    /**
     * 取消入职
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        onboardingService.cancelOnboarding(id);
        return Result.success();
    }

    /**
     * 查询入职流程详情
     */
    @GetMapping("/detail/{id}")
    public Result<HrOnboarding> detail(@PathVariable Long id) {
        return Result.success(onboardingService.getById(id));
    }

    /**
     * 按员工查询入职流程
     */
    @GetMapping("/byEmployee/{employeeId}")
    public Result<HrOnboarding> getByEmployee(@PathVariable Long employeeId) {
        return Result.success(onboardingService.getByEmployeeId(employeeId));
    }

    /**
     * 查询待入职列表
     */
    @GetMapping("/pendingList")
    public Result<java.util.List<HrOnboarding>> pendingList() {
        return Result.success(onboardingService.getPendingOnboardings());
    }
}
