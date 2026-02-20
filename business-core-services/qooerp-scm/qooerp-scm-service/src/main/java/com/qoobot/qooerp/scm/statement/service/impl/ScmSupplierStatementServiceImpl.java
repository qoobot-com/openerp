package com.qoobot.qooerp.scm.statement.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.statement.domain.ScmSupplierStatement;
import com.qoobot.qooerp.scm.statement.domain.ScmSupplierStatementDetail;
import com.qoobot.qooerp.scm.statement.dto.StatementStatisticsDTO;
import com.qoobot.qooerp.scm.statement.dto.SupplierStatementDTO;
import com.qoobot.qooerp.scm.statement.dto.SupplierStatementDetailDTO;
import com.qoobot.qooerp.scm.statement.dto.SupplierStatementQueryDTO;
import com.qoobot.qooerp.scm.statement.mapper.ScmSupplierStatementDetailMapper;
import com.qoobot.qooerp.scm.statement.mapper.ScmSupplierStatementMapper;
import com.qoobot.qooerp.scm.statement.service.IScmSupplierStatementService;
import com.qoobot.qooerp.scm.supplier.domain.ScmSupplier;
import com.qoobot.qooerp.scm.supplier.mapper.ScmSupplierMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 供应商对账Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScmSupplierStatementServiceImpl implements IScmSupplierStatementService {
    
    private final ScmSupplierStatementMapper statementMapper;
    private final ScmSupplierStatementDetailMapper statementDetailMapper;
    private final ScmSupplierMapper supplierMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> createStatement(SupplierStatementDTO dto) {
        // 验证供应商
        ScmSupplier supplier = supplierMapper.selectById(dto.getSupplierId());
        if (supplier == null) {
            return Result.fail(404, "供应商不存在");
        }
        
        // 生成对账单号
        String statementCode = generateStatementCode();
        
        // 创建对账单主表
        ScmSupplierStatement statement = new ScmSupplierStatement();
        BeanUtil.copyProperties(dto, statement);
        statement.setStatementCode(statementCode);
        statement.setSupplierCode(supplier.getSupplierCode());
        statement.setSupplierName(supplier.getSupplierName());
        statement.setAuditStatus("PENDING");
        statement.setSettlementStatus("PENDING");
        
        // 计算总金额
        BigDecimal totalAmount = statement.getPurchaseAmount()
                .subtract(statement.getReturnAmount())
                .add(statement.getFreightAmount() != null ? statement.getFreightAmount() : BigDecimal.ZERO)
                .add(statement.getOtherAmount() != null ? statement.getOtherAmount() : BigDecimal.ZERO);
        statement.setTotalAmount(totalAmount);
        
        statementMapper.insert(statement);
        
        return Result.success(statement.getId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> autoGenerateStatement(Long supplierId, String statementType, 
                                                                  Integer statementYear, Integer statementMonth) {
        // 验证供应商
        ScmSupplier supplier = supplierMapper.selectById(supplierId);
        if (supplier == null) {
            return Result.fail(404, "供应商不存在");
        }
        
        // 计算对账周期
        LocalDate startDate;
        LocalDate endDate;
        
        if ("MONTHLY".equals(statementType)) {
            startDate = LocalDate.of(statementYear, statementMonth, 1);
            endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        } else if ("QUARTERLY".equals(statementType)) {
            int quarter = (statementMonth - 1) / 3 + 1;
            startDate = LocalDate.of(statementYear, (quarter - 1) * 3 + 1, 1);
            endDate = startDate.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        } else {
            startDate = LocalDate.of(statementYear, 1, 1);
            endDate = LocalDate.of(statementYear, 12, 31);
        }
        
        // 生成对账单号
        String statementCode = generateStatementCode();
        
        // TODO: 调用Purchase模块获取采购、入库、退货数据
        // List<PurchaseOrderDTO> purchaseOrders = purchaseService.getPurchaseOrders(supplierId, startDate, endDate);
        // List<PurchaseReceiptDTO> receipts = purchaseService.getPurchaseReceipts(supplierId, startDate, endDate);
        // List<PurchaseReturnDTO> returns = purchaseService.getPurchaseReturns(supplierId, startDate, endDate);
        
        // 模拟数据
        BigDecimal purchaseAmount = new BigDecimal("50000.00");
        BigDecimal returnAmount = new BigDecimal("2000.00");
        BigDecimal freightAmount = new BigDecimal("500.00");
        BigDecimal otherAmount = new BigDecimal("100.00");
        BigDecimal totalAmount = purchaseAmount.subtract(returnAmount).add(freightAmount).add(otherAmount);
        
        // 创建对账单主表
        ScmSupplierStatement statement = new ScmSupplierStatement();
        statement.setStatementCode(statementCode);
        statement.setSupplierId(supplierId);
        statement.setSupplierCode(supplier.getSupplierCode());
        statement.setSupplierName(supplier.getSupplierName());
        statement.setStatementType(statementType);
        statement.setStartDate(startDate);
        statement.setEndDate(endDate);
        statement.setStatementDate(LocalDate.now());
        statement.setPurchaseAmount(purchaseAmount);
        statement.setReturnAmount(returnAmount);
        statement.setFreightAmount(freightAmount);
        statement.setOtherAmount(otherAmount);
        statement.setTotalAmount(totalAmount);
        statement.setAuditStatus("PENDING");
        statement.setSettlementStatus("PENDING");
        
        statementMapper.insert(statement);
        
        // TODO: 根据采购数据生成对账明细
        
        Map<String, Object> result = new HashMap<>();
        result.put("statementId", statement.getId());
        result.put("statementCode", statementCode);
        result.put("totalAmount", totalAmount);
        
        return Result.success(result);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateStatement(Long id, SupplierStatementDTO dto) {
        ScmSupplierStatement statement = statementMapper.selectById(id);
        if (statement == null) {
            return Result.fail(404, "对账单不存在");
        }

        if (!"PENDING".equals(statement.getAuditStatus())) {
            return Result.fail(400, "对账单已审核，不能修改");
        }
        
        // 更新主表
        BeanUtil.copyProperties(dto, statement, "id", "statementCode", "supplierId", "createTime");
        
        // 重新计算总金额
        BigDecimal totalAmount = statement.getPurchaseAmount()
                .subtract(statement.getReturnAmount())
                .add(statement.getFreightAmount() != null ? statement.getFreightAmount() : BigDecimal.ZERO)
                .add(statement.getOtherAmount() != null ? statement.getOtherAmount() : BigDecimal.ZERO);
        statement.setTotalAmount(totalAmount);
        
        statementMapper.updateById(statement);
        
        return Result.success();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteStatement(Long id) {
        statementMapper.deleteById(id);
        statementDetailMapper.delete(new LambdaQueryWrapper<ScmSupplierStatementDetail>()
                .eq(ScmSupplierStatementDetail::getStatementId, id));
        return Result.success();
    }
    
    @Override
    public Result<SupplierStatementDTO> getStatement(Long id) {
        ScmSupplierStatement statement = statementMapper.selectById(id);
        if (statement == null) {
            return Result.fail(404, "对账单不存在");
        }
        
        SupplierStatementDTO dto = BeanUtil.copyProperties(statement, SupplierStatementDTO.class);
        
        // 查询明细
        List<ScmSupplierStatementDetail> details = statementDetailMapper.selectList(
                new LambdaQueryWrapper<ScmSupplierStatementDetail>()
                        .eq(ScmSupplierStatementDetail::getStatementId, id)
        );
        
        List<SupplierStatementDetailDTO> detailDTOs = details.stream()
                .map(detail -> BeanUtil.copyProperties(detail, SupplierStatementDetailDTO.class))
                .collect(Collectors.toList());
        
        dto.setDetails(detailDTOs);
        
        return Result.success(dto);
    }
    
    @Override
    public Result<PageResult<SupplierStatementDTO>> queryStatements(SupplierStatementQueryDTO queryDTO) {
        Page<ScmSupplierStatement> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        
        LambdaQueryWrapper<ScmSupplierStatement> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getStatementCode()), 
                      ScmSupplierStatement::getStatementCode, queryDTO.getStatementCode())
               .eq(queryDTO.getSupplierId() != null, 
                   ScmSupplierStatement::getSupplierId, queryDTO.getSupplierId())
               .like(StrUtil.isNotBlank(queryDTO.getSupplierName()), 
                     ScmSupplierStatement::getSupplierName, queryDTO.getSupplierName())
               .eq(StrUtil.isNotBlank(queryDTO.getStatementType()), 
                   ScmSupplierStatement::getStatementType, queryDTO.getStatementType())
               .eq(StrUtil.isNotBlank(queryDTO.getAuditStatus()), 
                   ScmSupplierStatement::getAuditStatus, queryDTO.getAuditStatus())
               .eq(StrUtil.isNotBlank(queryDTO.getSettlementStatus()), 
                   ScmSupplierStatement::getSettlementStatus, queryDTO.getSettlementStatus())
               .ge(queryDTO.getStartDate() != null, 
                   ScmSupplierStatement::getStatementDate, queryDTO.getStartDate())
               .le(queryDTO.getEndDate() != null, 
                   ScmSupplierStatement::getStatementDate, queryDTO.getEndDate())
               .orderByDesc(ScmSupplierStatement::getStatementDate);
        
        Page<ScmSupplierStatement> resultPage = statementMapper.selectPage(page, wrapper);
        
        List<SupplierStatementDTO> records = resultPage.getRecords().stream()
                .map(s -> BeanUtil.copyProperties(s, SupplierStatementDTO.class))
                .collect(Collectors.toList());

        return Result.success(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveStatement(Long id, String auditStatus, String auditRemark) {
        ScmSupplierStatement statement = statementMapper.selectById(id);
        if (statement == null) {
            return Result.fail(404, "对账单不存在");
        }
        
        statement.setAuditStatus(auditStatus);
        statement.setAuditRemark(auditRemark);
        statement.setAuditTime(java.time.LocalDateTime.now());
        statementMapper.updateById(statement);
        
        return Result.success();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> settleStatement(Long id, String settlementMethod, LocalDate settlementDate) {
        ScmSupplierStatement statement = statementMapper.selectById(id);
        if (statement == null) {
            return Result.fail(404, "对账单不存在");
        }

        if (!"APPROVED".equals(statement.getAuditStatus())) {
            return Result.fail("对账单未审核通过");
        }

        if ("SETTLED".equals(statement.getSettlementStatus())) {
            return Result.fail(400, "对账单已结算");
        }
        
        // TODO: 调用Finance模块生成应付账款
        // Result<Long> payableResult = financeService.createPayable(statement.getId());
        Long payableId = 2001L; // 模拟
        String payableCode = "AP" + settlementDate.format(DATE_FORMATTER) + IdUtil.randomUUID().substring(0, 4).toUpperCase();
        
        // 更新对账单
        statement.setSettlementStatus("SETTLED");
        statement.setSettlementDate(settlementDate);
        statement.setSettlementMethod(settlementMethod);
        statementMapper.updateById(statement);
        
        Map<String, Object> result = new HashMap<>();
        result.put("payableId", payableId);
        result.put("payableCode", payableCode);
        
        return Result.success(result);
    }
    
    @Override
    public Result<StatementStatisticsDTO> getStatistics() {
        StatementStatisticsDTO statistics = new StatementStatisticsDTO();
        
        // 统计对账单总数
        Long totalStatements = statementMapper.selectCount(new LambdaQueryWrapper<>());
        statistics.setTotalStatements(totalStatements);
        
        // 统计待审核对账单
        Long pendingStatements = statementMapper.selectCount(
                new LambdaQueryWrapper<ScmSupplierStatement>()
                        .eq(ScmSupplierStatement::getAuditStatus, "PENDING")
        );
        statistics.setPendingStatements(pendingStatements);
        
        // 统计待结算对账单
        Long pendingSettlements = statementMapper.selectCount(
                new LambdaQueryWrapper<ScmSupplierStatement>()
                        .eq(ScmSupplierStatement::getAuditStatus, "APPROVED")
                        .eq(ScmSupplierStatement::getSettlementStatus, "PENDING")
        );
        statistics.setPendingSettlements(pendingSettlements);
        
        // 统计已结算总金额
        List<ScmSupplierStatement> settledList = statementMapper.selectList(
                new LambdaQueryWrapper<ScmSupplierStatement>()
                        .eq(ScmSupplierStatement::getSettlementStatus, "SETTLED")
        );
        BigDecimal settledAmount = settledList.stream()
                .map(ScmSupplierStatement::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.setSettledAmount(settledAmount);
        
        // 统计未结算总金额
        List<ScmSupplierStatement> unsettledList = statementMapper.selectList(
                new LambdaQueryWrapper<ScmSupplierStatement>()
                        .eq(ScmSupplierStatement::getAuditStatus, "APPROVED")
                        .eq(ScmSupplierStatement::getSettlementStatus, "PENDING")
        );
        BigDecimal unsettledAmount = unsettledList.stream()
                .map(ScmSupplierStatement::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.setUnsettledAmount(unsettledAmount);
        
        // 统计对账总金额
        BigDecimal totalAmount = settledList.stream()
                .map(ScmSupplierStatement::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(unsettledAmount);
        statistics.setTotalAmount(totalAmount);
        
        return Result.success(statistics);
    }
    
    /**
     * 生成对账单号
     */
    private String generateStatementCode() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomNum = String.format("%03d", new Random().nextInt(1000));
        return "ST" + dateStr + randomNum;
    }
}
