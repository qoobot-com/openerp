package com.qoobot.qooerp.hr.lifecycle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.lifecycle.domain.HrTransfer;
import java.util.List;

/**
 * 调动管理服务接口
 */
public interface IHrTransferService extends IService<HrTransfer> {

    /**
     * 创建调动申请
     */
    HrTransfer createTransfer(HrTransfer transfer);

    /**
     * 更新调动申请
     */
    void updateTransfer(HrTransfer transfer);

    /**
     * 删除调动申请
     */
    void deleteTransfer(Long id);

    /**
     * 提交审批
     */
    void submitApproval(Long id);

    /**
     * 审批通过
     */
    void approveTransfer(Long id, String approver, String remark);

    /**
     * 审批拒绝
     */
    void rejectTransfer(Long id, String approver, String remark);

    /**
     * 撤销申请
     */
    void cancelTransfer(Long id);

    /**
     * 按员工ID查询调动记录
     */
    List<HrTransfer> getByEmployeeId(Long employeeId);

    /**
     * 查询待审批列表
     */
    List<HrTransfer> getPendingTransfers();
}
