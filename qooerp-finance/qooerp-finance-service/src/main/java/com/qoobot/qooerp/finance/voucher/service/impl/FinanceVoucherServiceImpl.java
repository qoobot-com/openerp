package com.qoobot.qooerp.finance.voucher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.finance.voucher.domain.FinanceVoucher;
import com.qoobot.qooerp.finance.voucher.domain.FinanceVoucherDetail;
import com.qoobot.qooerp.finance.voucher.dto.FinanceVoucherDTO;
import com.qoobot.qooerp.finance.voucher.dto.FinanceVoucherDetailDTO;
import com.qoobot.qooerp.finance.voucher.dto.FinanceVoucherQueryDTO;
import com.qoobot.qooerp.finance.voucher.mapper.FinanceVoucherDetailMapper;
import com.qoobot.qooerp.finance.voucher.mapper.FinanceVoucherMapper;
import com.qoobot.qooerp.finance.voucher.service.IFinanceVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 财务凭证服务实现
 */
@Service
@RequiredArgsConstructor
public class FinanceVoucherServiceImpl extends ServiceImpl<FinanceVoucherMapper, FinanceVoucher> implements IFinanceVoucherService {

    private final FinanceVoucherDetailMapper voucherDetailMapper;

    @Override
    public PageResult<FinanceVoucher> queryPage(FinanceVoucherQueryDTO queryDTO) {
        LambdaQueryWrapper<FinanceVoucher> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getVoucherCode() != null && !queryDTO.getVoucherCode().isEmpty()) {
            wrapper.like(FinanceVoucher::getVoucherCode, queryDTO.getVoucherCode());
        }
        if (queryDTO.getVoucherType() != null && !queryDTO.getVoucherType().isEmpty()) {
            wrapper.eq(FinanceVoucher::getVoucherType, queryDTO.getVoucherType());
        }
        if (queryDTO.getAuditStatus() != null && !queryDTO.getAuditStatus().isEmpty()) {
            wrapper.eq(FinanceVoucher::getReviewStatus, queryDTO.getAuditStatus());
        }
        if (queryDTO.getPostingStatus() != null && !queryDTO.getPostingStatus().isEmpty()) {
            wrapper.eq(FinanceVoucher::getPostingStatus, queryDTO.getPostingStatus());
        }
        if (queryDTO.getSummary() != null && !queryDTO.getSummary().isEmpty()) {
            wrapper.like(FinanceVoucher::getSummary, queryDTO.getSummary());
        }
        if (queryDTO.getStartDate() != null) {
            wrapper.ge(FinanceVoucher::getVoucherDate, queryDTO.getStartDate());
        }
        if (queryDTO.getEndDate() != null) {
            wrapper.le(FinanceVoucher::getVoucherDate, queryDTO.getEndDate());
        }

        wrapper.orderByDesc(FinanceVoucher::getVoucherDate);

        Page<FinanceVoucher> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        Page<FinanceVoucher> result = page(page, wrapper);

        return new PageResult<>(page.getCurrent(), page.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FinanceVoucherDTO dto) {
        FinanceVoucher voucher = new FinanceVoucher();
        BeanUtils.copyProperties(dto, voucher);

        // 设置凭证状态
        voucher.setReviewStatus("DRAFT");
        voucher.setPostingStatus("UNPOSTED");

        save(voucher);

        // 保存凭证明细
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            validateVoucherBalance(dto.getDetails());

            BigDecimal debitTotal = BigDecimal.ZERO;
            BigDecimal creditTotal = BigDecimal.ZERO;

            for (FinanceVoucherDetailDTO detailDTO : dto.getDetails()) {
                FinanceVoucherDetail detail = new FinanceVoucherDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setVoucherId(voucher.getId());
                voucherDetailMapper.insert(detail);

                if (detailDTO.getDebitAmount() != null) {
                    debitTotal = debitTotal.add(detailDTO.getDebitAmount());
                }
                if (detailDTO.getCreditAmount() != null) {
                    creditTotal = creditTotal.add(detailDTO.getCreditAmount());
                }
            }

            voucher.setDebitAmount(debitTotal);
            voucher.setCreditAmount(creditTotal);
            updateById(voucher);
        }

        return voucher.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(FinanceVoucherDTO dto) {
        FinanceVoucher voucher = getById(dto.getId());
        if (voucher == null) {
            throw new BusinessException("凭证不存在");
        }
        if ("POSTED".equals(voucher.getPostingStatus())) {
            throw new BusinessException("已记账的凭证不能修改");
        }

        BeanUtils.copyProperties(dto, voucher);

        return updateById(voucher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        FinanceVoucher voucher = getById(id);
        if (voucher == null) {
            throw new BusinessException("凭证不存在");
        }
        if ("POSTED".equals(voucher.getPostingStatus())) {
            throw new BusinessException("已记账的凭证不能删除");
        }

        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submit(Long id) {
        FinanceVoucher voucher = getById(id);
        if (voucher == null) {
            throw new BusinessException("凭证不存在");
        }
        if (!"DRAFT".equals(voucher.getReviewStatus())) {
            throw new BusinessException("只有草稿状态的凭证才能提交审核");
        }

        voucher.setReviewStatus("PENDING");
        return updateById(voucher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean review(Long id, Boolean approved, String reviewComment) {
        FinanceVoucher voucher = getById(id);
        if (voucher == null) {
            throw new BusinessException("凭证不存在");
        }
        if (!"PENDING".equals(voucher.getReviewStatus())) {
            throw new BusinessException("只有待审核状态的凭证才能审核");
        }

        if (approved) {
            voucher.setReviewStatus("APPROVED");
        } else {
            voucher.setReviewStatus("REJECTED");
        }

        voucher.setReviewTime(LocalDateTime.now());
        return updateById(voucher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean posting(Long id) {
        FinanceVoucher voucher = getById(id);
        if (voucher == null) {
            throw new BusinessException("凭证不存在");
        }
        if (!"APPROVED".equals(voucher.getReviewStatus())) {
            throw new BusinessException("只有已审核的凭证才能记账");
        }
        if ("POSTED".equals(voucher.getPostingStatus())) {
            throw new BusinessException("凭证已记账，不能重复记账");
        }

        voucher.setPostingStatus("POSTED");
        voucher.setPostingTime(LocalDateTime.now());
        updateById(voucher);

        // TODO: 生成会计分录，更新科目余额
        generateAccountEntries(voucher);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unPosting(Long id) {
        FinanceVoucher voucher = getById(id);
        if (voucher == null) {
            throw new BusinessException("凭证不存在");
        }
        if (!"POSTED".equals(voucher.getPostingStatus())) {
            throw new BusinessException("只有已记账的凭证才能反记账");
        }

        // TODO: 删除会计分录，回滚科目余额
        revertAccountEntries(voucher);

        voucher.setPostingStatus("UNPOSTED");
        return updateById(voucher);
    }

    @Override
    public FinanceVoucher getDetail(Long id) {
        FinanceVoucher voucher = getById(id);
        if (voucher != null) {
            LambdaQueryWrapper<FinanceVoucherDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FinanceVoucherDetail::getVoucherId, id);
            List<FinanceVoucherDetail> details = voucherDetailMapper.selectList(wrapper);
            voucher.setDetails(details);
        }
        return voucher;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateAutoVoucher(String voucherType, String businessType, Long relatedOrderId) {
        // TODO: 根据业务类型自动生成凭证
        FinanceVoucher voucher = new FinanceVoucher();
        voucher.setVoucherType(voucherType);
        voucher.setReviewStatus("DRAFT");
        voucher.setPostingStatus("UNPOSTED");

        save(voucher);
        return voucher.getId();
    }

    /**
     * 验证凭证借贷平衡
     */
    private void validateVoucherBalance(List<FinanceVoucherDetailDTO> details) {
        if (details == null || details.isEmpty()) {
            throw new BusinessException("凭证明细不能为空");
        }

        BigDecimal debitTotal = BigDecimal.ZERO;
        BigDecimal creditTotal = BigDecimal.ZERO;

        for (FinanceVoucherDetailDTO detail : details) {
            if (detail.getDebitAmount() != null) {
                debitTotal = debitTotal.add(detail.getDebitAmount());
            }
            if (detail.getCreditAmount() != null) {
                creditTotal = creditTotal.add(detail.getCreditAmount());
            }
        }

        if (debitTotal.compareTo(creditTotal) != 0) {
            throw new BusinessException(String.format("借贷不平衡：借方金额=%s，贷方金额=%s", debitTotal, creditTotal));
        }
    }

    /**
     * 生成会计分录
     */
    private void generateAccountEntries(FinanceVoucher voucher) {
        // TODO: 根据凭证明细生成会计分录
        // TODO: 更新科目余额表
    }

    /**
     * 反记账，回滚会计分录
     */
    private void revertAccountEntries(FinanceVoucher voucher) {
        // TODO: 删除会计分录
        // TODO: 回滚科目余额表
    }
}
