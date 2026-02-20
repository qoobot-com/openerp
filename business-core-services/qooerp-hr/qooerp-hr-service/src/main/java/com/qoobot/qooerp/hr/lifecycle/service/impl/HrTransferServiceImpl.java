package com.qoobot.qooerp.hr.lifecycle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.lifecycle.domain.HrTransfer;
import com.qoobot.qooerp.hr.lifecycle.mapper.HrTransferMapper;
import com.qoobot.qooerp.hr.lifecycle.service.IHrTransferService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 调动管理服务实现
 */
@Service
public class HrTransferServiceImpl extends ServiceImpl<HrTransferMapper, HrTransfer> implements IHrTransferService {

    @Override
    public HrTransfer createTransfer(HrTransfer transfer) {
        String transferNo = generateTransferNo();
        transfer.setTransferNo(transferNo);
        transfer.setStatus("DRAFT");
        transfer.setCreateTime(java.time.LocalDateTime.now());
        save(transfer);
        return transfer;
    }

    @Override
    public void updateTransfer(HrTransfer transfer) {
        transfer.setUpdateTime(java.time.LocalDateTime.now());
        updateById(transfer);
    }

    @Override
    public void deleteTransfer(Long id) {
        removeById(id);
    }

    @Override
    public void submitApproval(Long id) {
        HrTransfer transfer = getById(id);
        if (transfer != null) {
            transfer.setStatus("PENDING");
            transfer.setUpdateTime(java.time.LocalDateTime.now());
            updateById(transfer);
        }
    }

    @Override
    public void approveTransfer(Long id, String approver, String remark) {
        HrTransfer transfer = getById(id);
        if (transfer != null) {
            transfer.setStatus("APPROVED");
            transfer.setApprover(approver);
            transfer.setApproveDate(LocalDate.now());
            transfer.setApproveRemark(remark);
            transfer.setUpdateTime(java.time.LocalDateTime.now());
            updateById(transfer);
            // TODO: 更新员工信息
        }
    }

    @Override
    public void rejectTransfer(Long id, String approver, String remark) {
        HrTransfer transfer = getById(id);
        if (transfer != null) {
            transfer.setStatus("REJECTED");
            transfer.setApprover(approver);
            transfer.setApproveDate(LocalDate.now());
            transfer.setApproveRemark(remark);
            transfer.setUpdateTime(java.time.LocalDateTime.now());
            updateById(transfer);
        }
    }

    @Override
    public void cancelTransfer(Long id) {
        HrTransfer transfer = getById(id);
        if (transfer != null && "PENDING".equals(transfer.getStatus())) {
            transfer.setStatus("CANCELLED");
            transfer.setUpdateTime(java.time.LocalDateTime.now());
            updateById(transfer);
        }
    }

    @Override
    public java.util.List<HrTransfer> getByEmployeeId(Long employeeId) {
        return lambdaQuery().eq(HrTransfer::getEmployeeId, employeeId)
            .orderByDesc(HrTransfer::getCreateTime).list();
    }

    @Override
    public java.util.List<HrTransfer> getPendingTransfers() {
        return lambdaQuery().eq(HrTransfer::getStatus, "PENDING").list();
    }

    /**
     * 生成调动单号
     */
    private String generateTransferNo() {
        return "TRN" + System.currentTimeMillis();
    }
}
