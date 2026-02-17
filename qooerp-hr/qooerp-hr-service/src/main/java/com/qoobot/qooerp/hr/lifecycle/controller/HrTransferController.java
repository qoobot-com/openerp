package com.qoobot.qooerp.hr.lifecycle.controller;

import com.qoobot.qooerp.common.core.domain.Result;
import com.qoobot.qooerp.hr.lifecycle.domain.HrTransfer;
import com.qoobot.qooerp.hr.lifecycle.service.IHrTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调动管理控制器
 */
@RestController
@RequestMapping("/hr/transfer")
public class HrTransferController {

    @Autowired
    private IHrTransferService transferService;

    /**
     * 创建调动申请
     */
    @PostMapping("/create")
    public Result<HrTransfer> create(@RequestBody HrTransfer transfer) {
        return Result.success(transferService.createTransfer(transfer));
    }

    /**
     * 更新调动申请
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody HrTransfer transfer) {
        transferService.updateTransfer(transfer);
        return Result.success();
    }

    /**
     * 删除调动申请
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return Result.success();
    }

    /**
     * 提交审批
     */
    @PostMapping("/submit/{id}")
    public Result<Void> submit(@PathVariable Long id) {
        transferService.submitApproval(id);
        return Result.success();
    }

    /**
     * 审批通过
     */
    @PostMapping("/approve/{id}")
    public Result<Void> approve(@PathVariable Long id, @RequestParam String approver, @RequestParam String remark) {
        transferService.approveTransfer(id, approver, remark);
        return Result.success();
    }

    /**
     * 审批拒绝
     */
    @PostMapping("/reject/{id}")
    public Result<Void> reject(@PathVariable Long id, @RequestParam String approver, @RequestParam String remark) {
        transferService.rejectTransfer(id, approver, remark);
        return Result.success();
    }

    /**
     * 撤销申请
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        transferService.cancelTransfer(id);
        return Result.success();
    }

    /**
     * 查询调动详情
     */
    @GetMapping("/detail/{id}")
    public Result<HrTransfer> detail(@PathVariable Long id) {
        return Result.success(transferService.getById(id));
    }

    /**
     * 按员工查询调动记录
     */
    @GetMapping("/byEmployee/{employeeId}")
    public Result<List<HrTransfer>> getByEmployee(@PathVariable Long employeeId) {
        return Result.success(transferService.getByEmployeeId(employeeId));
    }

    /**
     * 查询待审批列表
     */
    @GetMapping("/pendingList")
    public Result<List<HrTransfer>> pendingList() {
        return Result.success(transferService.getPendingTransfers());
    }
}
