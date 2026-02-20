package com.qoobot.qooerp.scm.supplier.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.scm.constant.ScmConstant;
import com.qoobot.qooerp.scm.supplier.domain.ScmSupplier;
import com.qoobot.qooerp.scm.supplier.domain.ScmSupplierEvaluation;
import com.qoobot.qooerp.scm.supplier.dto.EvaluationDTO;
import com.qoobot.qooerp.scm.supplier.dto.SupplierDTO;
import com.qoobot.qooerp.scm.supplier.dto.SupplierQueryDTO;
import com.qoobot.qooerp.scm.supplier.mapper.ScmSupplierEvaluationMapper;
import com.qoobot.qooerp.scm.supplier.mapper.ScmSupplierMapper;
import com.qoobot.qooerp.scm.supplier.service.IScmSupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 供应商Service实现
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScmSupplierServiceImpl extends ServiceImpl<ScmSupplierMapper, ScmSupplier> implements IScmSupplierService {

    private final ScmSupplierEvaluationMapper evaluationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSupplier(SupplierDTO dto) {
        // 生成供应商编码
        String supplierCode = generateSupplierCode();

        // 检查编码是否重复
        ScmSupplier exist = getByCode(supplierCode);
        if (exist != null) {
            throw new BusinessException("供应商编码已存在");
        }

        ScmSupplier supplier = BeanUtil.copyProperties(dto, ScmSupplier.class);
        supplier.setSupplierCode(supplierCode);
        supplier.setCreditLevel(ScmConstant.CREDIT_LEVEL_C);
        supplier.setEvaluationScore(70);
        supplier.setQualityScore(70);
        supplier.setDeliveryScore(70);
        supplier.setServiceScore(70);
        supplier.setPriceScore(70);
        supplier.setStatus(ScmConstant.STATUS_ACTIVE);
        supplier.setTenantId(SecurityUtils.getTenantId());
        supplier.setDeleted(0);

        save(supplier);

        log.info("创建供应商成功，供应商ID：{}，供应商编码：{}", supplier.getId(), supplierCode);
        return supplier.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSupplier(Long id, SupplierDTO dto) {
        ScmSupplier supplier = getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }

        ScmSupplier update = BeanUtil.copyProperties(dto, ScmSupplier.class);
        update.setId(id);
        update.setSupplierCode(supplier.getSupplierCode());

        boolean result = updateById(update);
        log.info("更新供应商成功，供应商ID：{}", id);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSupplier(Long id) {
        ScmSupplier supplier = getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }

        boolean result = removeById(id);
        log.info("删除供应商成功，供应商ID：{}", id);
        return result;
    }

    @Override
    public SupplierDTO getSupplier(Long id) {
        ScmSupplier supplier = getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }

        return BeanUtil.copyProperties(supplier, SupplierDTO.class);
    }

    @Override
    public PageResult<ScmSupplier> querySuppliers(SupplierQueryDTO queryDTO) {
        Page<ScmSupplier> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<ScmSupplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getStatus() != null, ScmSupplier::getStatus, queryDTO.getStatus())
                .like(queryDTO.getSupplierCode() != null, ScmSupplier::getSupplierCode, queryDTO.getSupplierCode())
                .like(queryDTO.getSupplierName() != null, ScmSupplier::getSupplierName, queryDTO.getSupplierName())
                .eq(queryDTO.getSupplierType() != null, ScmSupplier::getSupplierType, queryDTO.getSupplierType())
                .eq(queryDTO.getCreditLevel() != null, ScmSupplier::getCreditLevel, queryDTO.getCreditLevel())
                .orderByDesc(ScmSupplier::getCreateTime);

        IPage<ScmSupplier> result = page(page, wrapper);

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean evaluateSupplier(Long supplierId, EvaluationDTO dto) {
        ScmSupplier supplier = getById(supplierId);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }

        // 计算综合评分
        int totalScore = (dto.getQualityScore() + dto.getDeliveryScore()
                + dto.getServiceScore() + dto.getPriceScore()) / 4;

        // 确定信用等级
        String creditLevel = determineCreditLevel(totalScore);

        // 保存评估记录
        ScmSupplierEvaluation evaluation = new ScmSupplierEvaluation();
        evaluation.setSupplierId(supplierId);
        evaluation.setSupplierCode(supplier.getSupplierCode());
        evaluation.setEvaluationDate(LocalDate.now());
        evaluation.setQualityScore(dto.getQualityScore());
        evaluation.setDeliveryScore(dto.getDeliveryScore());
        evaluation.setServiceScore(dto.getServiceScore());
        evaluation.setPriceScore(dto.getPriceScore());
        evaluation.setTotalScore(totalScore);
        evaluation.setCreditLevel(creditLevel);
        evaluation.setEvaluator(SecurityUtils.getUsername());
        evaluation.setEvaluationRemark(dto.getEvaluationRemark());
        evaluation.setTenantId(SecurityUtils.getTenantId());
        evaluation.setDeleted(0);

        evaluationMapper.insert(evaluation);

        // 更新供应商评分
        supplier.setEvaluationScore(totalScore);
        supplier.setQualityScore(dto.getQualityScore());
        supplier.setDeliveryScore(dto.getDeliveryScore());
        supplier.setServiceScore(dto.getServiceScore());
        supplier.setPriceScore(dto.getPriceScore());
        supplier.setCreditLevel(creditLevel);
        supplier.setLastEvaluationTime(java.time.LocalDateTime.now());
        updateById(supplier);

        log.info("评估供应商成功，供应商ID：{}，评分：{}，等级：{}", supplierId, totalScore, creditLevel);
        return true;
    }

    @Override
    public List<ScmSupplierEvaluation> getEvaluationHistory(Long supplierId) {
        LambdaQueryWrapper<ScmSupplierEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScmSupplierEvaluation::getSupplierId, supplierId)
                .orderByDesc(ScmSupplierEvaluation::getEvaluationDate);
        return evaluationMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        ScmSupplier supplier = getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }

        supplier.setStatus(status);
        boolean result = updateById(supplier);
        log.info("更新供应商状态成功，供应商ID：{}，状态：{}", id, status);
        return result;
    }

    @Override
    public String generateSupplierCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomNum = String.format("%03d", IdUtil.getSnowflakeNextId() % 1000);
        return "SP" + date + randomNum;
    }

    /**
     * 根据评分确定信用等级
     */
    private String determineCreditLevel(int score) {
        if (score >= 90) {
            return ScmConstant.CREDIT_LEVEL_A;
        } else if (score >= 80) {
            return ScmConstant.CREDIT_LEVEL_B;
        } else if (score >= 70) {
            return ScmConstant.CREDIT_LEVEL_C;
        } else {
            return ScmConstant.CREDIT_LEVEL_D;
        }
    }

    /**
     * 根据编码查询供应商
     */
    private ScmSupplier getByCode(String supplierCode) {
        LambdaQueryWrapper<ScmSupplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScmSupplier::getSupplierCode, supplierCode);
        return getOne(wrapper);
    }
}
