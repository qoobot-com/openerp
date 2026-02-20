package com.qoobot.qooerp.scm.quotation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.constant.ScmConstant;
import com.qoobot.qooerp.scm.quotation.domain.ScmSupplierQuotation;
import com.qoobot.qooerp.scm.quotation.domain.ScmSupplierQuotationDetail;
import com.qoobot.qooerp.scm.quotation.dto.QuotationComparisonDTO;
import com.qoobot.qooerp.scm.quotation.dto.SupplierQuotationDTO;
import com.qoobot.qooerp.scm.quotation.dto.SupplierQuotationDetailDTO;
import com.qoobot.qooerp.scm.quotation.dto.SupplierQuotationQueryDTO;
import com.qoobot.qooerp.scm.quotation.mapper.ScmSupplierQuotationDetailMapper;
import com.qoobot.qooerp.scm.quotation.mapper.ScmSupplierQuotationMapper;
import com.qoobot.qooerp.scm.quotation.service.IScmSupplierQuotationService;
import com.qoobot.qooerp.scm.supplier.domain.ScmSupplier;
import com.qoobot.qooerp.scm.supplier.mapper.ScmSupplierMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 供应商报价Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScmSupplierQuotationServiceImpl implements IScmSupplierQuotationService {
    
    private final ScmSupplierQuotationMapper quotationMapper;
    private final ScmSupplierQuotationDetailMapper quotationDetailMapper;
    private final ScmSupplierMapper supplierMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> createQuotation(SupplierQuotationDTO dto) {
        // 验证供应商
        ScmSupplier supplier = supplierMapper.selectById(dto.getSupplierId());
        if (supplier == null) {
            return Result.fail(404, "供应商不存在");
        }
        
        // 生成报价单号
        String quotationCode = generateQuotationCode();
        
        // 创建报价单主表
        ScmSupplierQuotation quotation = new ScmSupplierQuotation();
        BeanUtil.copyProperties(dto, quotation);
        quotation.setQuotationCode(quotationCode);
        quotation.setSupplierCode(supplier.getSupplierCode());
        quotation.setSupplierName(supplier.getSupplierName());
        quotation.setAuditStatus("PENDING");
        quotation.setConverted(0);
        quotation.setTotalAmount(calculateTotalAmount(dto.getDetails()));
        quotation.setCreateTime(LocalDateTime.now());
        
        quotationMapper.insert(quotation);
        
        // 创建报价明细
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (SupplierQuotationDetailDTO detailDTO : dto.getDetails()) {
                ScmSupplierQuotationDetail detail = new ScmSupplierQuotationDetail();
                BeanUtil.copyProperties(detailDTO, detail);
                detail.setQuotationId(quotation.getId());
                detail.setQuotationCode(quotationCode);
                detail.setAmount(detail.getQuantity().multiply(detail.getUnitPrice()));
                detail.setCreateTime(LocalDateTime.now());
                quotationDetailMapper.insert(detail);
            }
        }
        
        return Result.success(quotation.getId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateQuotation(Long id, SupplierQuotationDTO dto) {
        ScmSupplierQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            return Result.fail(404, "报价单不存在");
        }

        if (!"PENDING".equals(quotation.getAuditStatus())) {
            return Result.fail(400, "报价单已审核，不能修改");
        }
        
        // 更新主表
        BeanUtil.copyProperties(dto, quotation, "id", "quotationCode", "supplierId", "createTime");
        quotation.setUpdateTime(LocalDateTime.now());
        quotationMapper.updateById(quotation);
        
        // 删除原明细
        quotationDetailMapper.delete(new LambdaQueryWrapper<ScmSupplierQuotationDetail>()
                .eq(ScmSupplierQuotationDetail::getQuotationId, id));
        
        // 插入新明细
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (SupplierQuotationDetailDTO detailDTO : dto.getDetails()) {
                ScmSupplierQuotationDetail detail = new ScmSupplierQuotationDetail();
                BeanUtil.copyProperties(detailDTO, detail);
                detail.setQuotationId(id);
                detail.setQuotationCode(quotation.getQuotationCode());
                detail.setAmount(detail.getQuantity().multiply(detail.getUnitPrice()));
                detail.setCreateTime(LocalDateTime.now());
                quotationDetailMapper.insert(detail);
            }
        }
        
        return Result.success();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteQuotation(Long id) {
        quotationMapper.deleteById(id);
        quotationDetailMapper.delete(new LambdaQueryWrapper<ScmSupplierQuotationDetail>()
                .eq(ScmSupplierQuotationDetail::getQuotationId, id));
        return Result.success();
    }
    
    @Override
    public Result<SupplierQuotationDTO> getQuotation(Long id) {
        ScmSupplierQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            return Result.fail(404, "报价单不存在");
        }
        
        SupplierQuotationDTO dto = BeanUtil.copyProperties(quotation, SupplierQuotationDTO.class);
        
        // 查询明细
        List<ScmSupplierQuotationDetail> details = quotationDetailMapper.selectList(
                new LambdaQueryWrapper<ScmSupplierQuotationDetail>()
                        .eq(ScmSupplierQuotationDetail::getQuotationId, id)
        );
        
        List<SupplierQuotationDetailDTO> detailDTOs = details.stream()
                .map(detail -> BeanUtil.copyProperties(detail, SupplierQuotationDetailDTO.class))
                .collect(Collectors.toList());
        
        dto.setDetails(detailDTOs);
        
        return Result.success(dto);
    }
    
    @Override
    public Result<PageResult<SupplierQuotationDTO>> queryQuotations(SupplierQuotationQueryDTO queryDTO) {
        Page<ScmSupplierQuotation> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        
        LambdaQueryWrapper<ScmSupplierQuotation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getQuotationCode()), 
                      ScmSupplierQuotation::getQuotationCode, queryDTO.getQuotationCode())
               .eq(queryDTO.getSupplierId() != null, 
                   ScmSupplierQuotation::getSupplierId, queryDTO.getSupplierId())
               .like(StrUtil.isNotBlank(queryDTO.getSupplierName()), 
                     ScmSupplierQuotation::getSupplierName, queryDTO.getSupplierName())
               .eq(StrUtil.isNotBlank(queryDTO.getAuditStatus()), 
                   ScmSupplierQuotation::getAuditStatus, queryDTO.getAuditStatus())
               .eq(queryDTO.getConverted() != null, 
                   ScmSupplierQuotation::getConverted, queryDTO.getConverted())
               .ge(queryDTO.getStartDate() != null, 
                   ScmSupplierQuotation::getQuotationDate, queryDTO.getStartDate())
               .le(queryDTO.getEndDate() != null, 
                   ScmSupplierQuotation::getQuotationDate, queryDTO.getEndDate())
               .orderByDesc(ScmSupplierQuotation::getCreateTime);
        
        Page<ScmSupplierQuotation> resultPage = quotationMapper.selectPage(page, wrapper);
        
        List<SupplierQuotationDTO> records = resultPage.getRecords().stream()
                .map(q -> BeanUtil.copyProperties(q, SupplierQuotationDTO.class))
                .collect(Collectors.toList());

        return Result.success(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveQuotation(Long id, String auditStatus, String auditRemark) {
        ScmSupplierQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            return Result.fail(404, "报价单不存在");
        }
        
        quotation.setAuditStatus(auditStatus);
        quotation.setAuditRemark(auditRemark);
        quotation.setAuditTime(LocalDateTime.now());
        quotation.setUpdateTime(LocalDateTime.now());
        quotationMapper.updateById(quotation);
        
        return Result.success();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> convertToPurchaseOrder(Long id) {
        ScmSupplierQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            return Result.fail(404, "报价单不存在");
        }

        if (!"APPROVED".equals(quotation.getAuditStatus())) {
            return Result.fail("报价单未审核通过");
        }

        if (quotation.getConverted() == 1) {
            return Result.fail(400, "报价单已转采购订单");
        }

        // 检查报价有效期
        if (quotation.getValidUntil() != null && LocalDate.now().isAfter(quotation.getValidUntil())) {
            return Result.fail(400, "报价已过期，不能转换");
        }
        
        // TODO: 调用Purchase模块创建采购订单
        // Result<Long> purchaseResult = purchaseService.createPurchaseOrderFromQuotation(quotation.getId());
        Long purchaseOrderId = 1001L; // 模拟
        String purchaseOrderCode = "PO" + LocalDate.now().format(DATE_FORMATTER) + IdUtil.randomUUID().substring(0, 4).toUpperCase();
        
        // 更新报价单
        quotation.setConverted(1);
        quotation.setPurchaseOrderId(purchaseOrderId);
        quotation.setPurchaseOrderCode(purchaseOrderCode);
        quotation.setConvertTime(LocalDateTime.now());
        quotation.setUpdateTime(LocalDateTime.now());
        quotationMapper.updateById(quotation);
        
        Map<String, Object> result = new HashMap<>();
        result.put("purchaseOrderId", purchaseOrderId);
        result.put("purchaseOrderCode", purchaseOrderCode);
        
        return Result.success(result);
    }
    
    @Override
    public Result<List<QuotationComparisonDTO>> compareQuotations(List<String> materialCodes, 
                                                                  LocalDate startDate, 
                                                                  LocalDate endDate) {
        // 查询相关报价
        LambdaQueryWrapper<ScmSupplierQuotation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScmSupplierQuotation::getAuditStatus, "APPROVED")
               .eq(ScmSupplierQuotation::getConverted, 0)
               .ge(startDate != null, ScmSupplierQuotation::getQuotationDate, startDate)
               .le(endDate != null, ScmSupplierQuotation::getQuotationDate, endDate);
        
        List<ScmSupplierQuotation> quotations = quotationMapper.selectList(wrapper);
        
        List<QuotationComparisonDTO> comparisonList = new ArrayList<>();
        
        for (String materialCode : materialCodes) {
            QuotationComparisonDTO comparison = new QuotationComparisonDTO();
            comparison.setMaterialCode(materialCode);
            
            List<QuotationComparisonDTO.SupplierQuotationInfoDTO> quotationInfos = new ArrayList<>();
            BigDecimal bestPrice = null;
            Long bestSupplierId = null;
            
            for (ScmSupplierQuotation quotation : quotations) {
                List<ScmSupplierQuotationDetail> details = quotationDetailMapper.selectList(
                        new LambdaQueryWrapper<ScmSupplierQuotationDetail>()
                                .eq(ScmSupplierQuotationDetail::getQuotationId, quotation.getId())
                                .eq(ScmSupplierQuotationDetail::getMaterialCode, materialCode)
                );
                
                for (ScmSupplierQuotationDetail detail : details) {
                    QuotationComparisonDTO.SupplierQuotationInfoDTO info = new QuotationComparisonDTO.SupplierQuotationInfoDTO();
                    info.setSupplierId(quotation.getSupplierId());
                    info.setSupplierName(quotation.getSupplierName());
                    info.setQuotationCode(quotation.getQuotationCode());
                    info.setUnitPrice(detail.getUnitPrice());
                    info.setDeliveryDays(detail.getDeliveryDays());
                    info.setQuotationDate(quotation.getQuotationDate());
                    info.setValidUntil(quotation.getValidUntil());
                    
                    quotationInfos.add(info);
                    
                    if (bestPrice == null || detail.getUnitPrice().compareTo(bestPrice) < 0) {
                        bestPrice = detail.getUnitPrice();
                        bestSupplierId = quotation.getSupplierId();
                    }
                }
            }
            
            if (quotationInfos.isEmpty()) {
                continue;
            }
            
            comparison.setQuotations(quotationInfos);
            comparison.setBestSupplierId(bestSupplierId);
            comparison.setBestUnitPrice(bestPrice);

            // 设置最优供应商名称
            final Long finalBestSupplierId = bestSupplierId;
            Optional<QuotationComparisonDTO.SupplierQuotationInfoDTO> bestSupplier = quotationInfos.stream()
                    .filter(q -> q.getSupplierId().equals(finalBestSupplierId))
                    .findFirst();
            bestSupplier.ifPresent(q -> comparison.setBestSupplierName(q.getSupplierName()));
            
            comparisonList.add(comparison);
        }
        
        return Result.success(comparisonList);
    }
    
    /**
     * 生成报价单号
     */
    private String generateQuotationCode() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomNum = String.format("%03d", new Random().nextInt(1000));
        return "QT" + dateStr + randomNum;
    }
    
    /**
     * 计算总金额
     */
    private BigDecimal calculateTotalAmount(List<SupplierQuotationDetailDTO> details) {
        if (details == null || details.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return details.stream()
                .map(d -> d.getQuantity().multiply(d.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
