package com.qoobot.qooerp.sales.quotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.sales.quotation.domain.SalesQuotation;
import com.qoobot.qooerp.sales.quotation.domain.SalesQuotationDetail;
import com.qoobot.qooerp.sales.quotation.dto.QuotationDTO;
import com.qoobot.qooerp.sales.quotation.dto.QuotationDetailDTO;
import com.qoobot.qooerp.sales.quotation.dto.QuotationQueryDTO;
import com.qoobot.qooerp.sales.quotation.mapper.SalesQuotationMapper;
import com.qoobot.qooerp.sales.quotation.service.SalesQuotationDetailService;
import com.qoobot.qooerp.sales.quotation.service.SalesQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 销售报价服务实现
 */
@Service
@RequiredArgsConstructor
public class SalesQuotationServiceImpl extends ServiceImpl<SalesQuotationMapper, SalesQuotation>
        implements SalesQuotationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();
    private final SalesQuotationDetailService quotationDetailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> createQuotation(QuotationDTO quotationDTO) {
        // 生成报价单编号
        String quotationCode = generateQuotationCode();

        // 创建报价单主表
        SalesQuotation quotation = new SalesQuotation();
        BeanUtils.copyProperties(quotationDTO, quotation);
        quotation.setQuotationCode(quotationCode);
        quotation.setQuotationStatus("DRAFT");
        quotation.setQuotationDate(LocalDate.now());

        // 计算报价金额
        BigDecimal quotationAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (quotationDTO.getDetails() != null && !quotationDTO.getDetails().isEmpty()) {
            for (QuotationDetailDTO detailDTO : quotationDTO.getDetails()) {
                // 计算明细金额
                BigDecimal amount = detailDTO.getQuantity().multiply(detailDTO.getUnitPrice());
                detailDTO.setAmount(amount);

                // 计算折扣金额
                if (detailDTO.getDiscountRate() != null && detailDTO.getDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal detailDiscountAmount = amount.multiply(detailDTO.getDiscountRate())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    detailDTO.setDiscountAmount(detailDiscountAmount);
                    discountAmount = discountAmount.add(detailDiscountAmount);
                }

                // 计算实付金额
                BigDecimal detailDiscountAmt = detailDTO.getDiscountAmount() != null ? detailDTO.getDiscountAmount() : BigDecimal.ZERO;
                BigDecimal payableAmount = amount.subtract(detailDiscountAmt);
                detailDTO.setPayableAmount(payableAmount);

                quotationAmount = quotationAmount.add(amount);
            }
        }

        quotation.setQuotationAmount(quotationAmount);
        quotation.setDiscountAmount(discountAmount);
        quotation.setPayableAmount(quotationAmount.subtract(discountAmount));

        save(quotation);

        // 保存报价单明细
        if (quotationDTO.getDetails() != null && !quotationDTO.getDetails().isEmpty()) {
            List<SalesQuotationDetail> details = quotationDTO.getDetails().stream()
                    .map(detailDTO -> {
                        SalesQuotationDetail detail = new SalesQuotationDetail();
                        BeanUtils.copyProperties(detailDTO, detail);
                        detail.setQuotationId(quotation.getId());
                        return detail;
                    })
                    .collect(Collectors.toList());

            details.forEach(detail -> quotationDetailService.save(detail));
        }

        return Result.success(quotation.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveQuotation(Long quotationId, Long approverId) {
        SalesQuotation quotation = getById(quotationId);
        if (quotation == null) {
            throw new BusinessException("报价单不存在");
        }

        if (!"DRAFT".equals(quotation.getQuotationStatus())) {
            throw new BusinessException("只有草稿状态的报价单才能审核");
        }

        quotation.setQuotationStatus("APPROVED");
        quotation.setUpdateBy(approverId);
        updateById(quotation);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> sendQuotation(Long quotationId) {
        SalesQuotation quotation = getById(quotationId);
        if (quotation == null) {
            throw new BusinessException("报价单不存在");
        }

        if (!"APPROVED".equals(quotation.getQuotationStatus())) {
            throw new BusinessException("只有审核通过的报价单才能发送");
        }

        quotation.setQuotationStatus("SENT");
        updateById(quotation);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> convertToOrder(Long quotationId) {
        SalesQuotation quotation = getById(quotationId);
        if (quotation == null) {
            throw new BusinessException("报价单不存在");
        }

        if (!"SENT".equals(quotation.getQuotationStatus())) {
            throw new BusinessException("只有已发送的报价单才能转订单");
        }

        quotation.setQuotationStatus("CONVERTED");
        updateById(quotation);

        // TODO: 创建销售订单逻辑
        // 1. 复制报价单信息到订单
        // 2. 复制报价明细到订单明细
        // 3. 关联订单ID到报价单

        return Result.success(quotationId);
    }

    @Override
    public Result<QuotationDTO> getQuotation(Long quotationId) {
        SalesQuotation quotation = getById(quotationId);
        if (quotation == null) {
            throw new BusinessException("报价单不存在");
        }

        QuotationDTO quotationDTO = new QuotationDTO();
        BeanUtils.copyProperties(quotation, quotationDTO);

        // 查询明细
        LambdaQueryWrapper<SalesQuotationDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(SalesQuotationDetail::getQuotationId, quotationId)
                .eq(SalesQuotationDetail::getDeleted, 0);
        List<SalesQuotationDetail> details = quotationDetailService.list(detailWrapper);

        List<QuotationDetailDTO> detailDTOs = details.stream()
                .map(detail -> {
                    QuotationDetailDTO dto = new QuotationDetailDTO();
                    BeanUtils.copyProperties(detail, dto);
                    return dto;
                })
                .collect(Collectors.toList());

        quotationDTO.setDetails(detailDTOs);

        return Result.success(quotationDTO);
    }

    @Override
    public Result<PageResult<QuotationDTO>> queryQuotations(QuotationQueryDTO queryDTO) {
        Page<SalesQuotation> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SalesQuotation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getCustomerId() != null, SalesQuotation::getCustomerId, queryDTO.getCustomerId())
                .like(queryDTO.getCustomerName() != null, SalesQuotation::getCustomerName, queryDTO.getCustomerName())
                .eq(queryDTO.getQuotationStatus() != null, SalesQuotation::getQuotationStatus, queryDTO.getQuotationStatus())
                .ge(queryDTO.getStartDate() != null, SalesQuotation::getQuotationDate, queryDTO.getStartDate())
                .le(queryDTO.getEndDate() != null, SalesQuotation::getQuotationDate, queryDTO.getEndDate())
                .orderByDesc(SalesQuotation::getCreateTime);

        IPage<SalesQuotation> quotationPage = page(page, queryWrapper);

        List<QuotationDTO> quotationDTOList = quotationPage.getRecords().stream()
                .map(quotation -> {
                    QuotationDTO dto = new QuotationDTO();
                    BeanUtils.copyProperties(quotation, dto);
                    return dto;
                })
                .collect(Collectors.toList());

        PageResult<QuotationDTO> pageResult = new PageResult<>();
        pageResult.setRecords(quotationDTOList);
        pageResult.setTotal(quotationPage.getTotal());
        pageResult.setCurrent(quotationPage.getCurrent());
        pageResult.setSize(quotationPage.getSize());

        return Result.success(pageResult);
    }

    /**
     * 生成报价单编号
     */
    private String generateQuotationCode() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomNum = String.format("%04d", RANDOM.nextInt(10000));
        return "QT" + dateStr + randomNum;
    }
}
