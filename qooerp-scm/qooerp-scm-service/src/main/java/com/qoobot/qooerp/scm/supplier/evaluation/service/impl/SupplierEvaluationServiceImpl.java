package com.qoobot.qooerp.scm.supplier.evaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.supplier.evaluation.domain.SupplierEvaluation;
import com.qoobot.qooerp.scm.supplier.evaluation.dto.EvaluationQueryDTO;
import com.qoobot.qooerp.scm.supplier.evaluation.dto.SupplierEvaluationDTO;
import com.qoobot.qooerp.scm.supplier.evaluation.mapper.SupplierEvaluationMapper;
import com.qoobot.qooerp.scm.supplier.evaluation.service.ISupplierEvaluationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * 供应商评估Service实现
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Service
public class SupplierEvaluationServiceImpl extends ServiceImpl<SupplierEvaluationMapper, SupplierEvaluation>
        implements ISupplierEvaluationService {

    // 评分权重配置
    private static final BigDecimal QUALITY_WEIGHT = new BigDecimal("0.30");  // 质量权重30%
    private static final BigDecimal DELIVERY_WEIGHT = new BigDecimal("0.30"); // 交期权重30%
    private static final BigDecimal PRICE_WEIGHT = new BigDecimal("0.20");    // 价格权重20%
    private static final BigDecimal SERVICE_WEIGHT = new BigDecimal("0.20");  // 服务权重20%

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SupplierEvaluationDTO dto) {
        if (dto.getSupplierId() == null) {
            throw new BusinessException("供应商ID不能为空");
        }

        // 设置默认评估日期
        if (dto.getEvaluationDate() == null) {
            dto.setEvaluationDate(LocalDate.now());
        }

        // 计算综合得分
        if (dto.getTotalScore() == null) {
            BigDecimal totalScore = calculateTotalScore(
                dto.getQualityScore(),
                dto.getDeliveryScore(),
                dto.getPriceScore(),
                dto.getServiceScore()
            );
            dto.setTotalScore(totalScore);
        }

        // 判定评估等级
        if (dto.getEvaluationLevel() == null && dto.getTotalScore() != null) {
            String level = evaluateLevel(dto.getTotalScore());
            dto.setEvaluationLevel(level);
        }

        SupplierEvaluation evaluation = new SupplierEvaluation();
        BeanUtils.copyProperties(dto, evaluation);

        save(evaluation);
        return evaluation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SupplierEvaluationDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("评估ID不能为空");
        }

        SupplierEvaluation evaluation = getById(dto.getId());
        if (evaluation == null) {
            throw new BusinessException("供应商评估不存在");
        }

        // 如果修改了评分，重新计算综合得分和等级
        if (dto.getQualityScore() != null || dto.getDeliveryScore() != null ||
            dto.getPriceScore() != null || dto.getServiceScore() != null) {

            BigDecimal qualityScore = dto.getQualityScore() != null ? dto.getQualityScore() : evaluation.getQualityScore();
            BigDecimal deliveryScore = dto.getDeliveryScore() != null ? dto.getDeliveryScore() : evaluation.getDeliveryScore();
            BigDecimal priceScore = dto.getPriceScore() != null ? dto.getPriceScore() : evaluation.getPriceScore();
            BigDecimal serviceScore = dto.getServiceScore() != null ? dto.getServiceScore() : evaluation.getServiceScore();

            BigDecimal totalScore = calculateTotalScore(qualityScore, deliveryScore, priceScore, serviceScore);
            dto.setTotalScore(totalScore);

            String level = evaluateLevel(totalScore);
            dto.setEvaluationLevel(level);
        }

        BeanUtils.copyProperties(dto, evaluation);
        return updateById(evaluation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        SupplierEvaluation evaluation = getById(id);
        if (evaluation == null) {
            throw new BusinessException("供应商评估不存在");
        }
        return removeById(id);
    }

    @Override
    public SupplierEvaluationDTO getDetail(Long id) {
        SupplierEvaluation evaluation = getById(id);
        if (evaluation == null) {
            throw new BusinessException("供应商评估不存在");
        }

        SupplierEvaluationDTO dto = new SupplierEvaluationDTO();
        BeanUtils.copyProperties(evaluation, dto);
        return dto;
    }

    @Override
    public PageResult<SupplierEvaluation> queryPage(EvaluationQueryDTO queryDTO) {
        LambdaQueryWrapper<SupplierEvaluation> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getSupplierId() != null) {
            wrapper.eq(SupplierEvaluation::getSupplierId, queryDTO.getSupplierId());
        }
        if (StringUtils.hasText(queryDTO.getEvaluationType())) {
            wrapper.eq(SupplierEvaluation::getEvaluationType, queryDTO.getEvaluationType());
        }
        if (StringUtils.hasText(queryDTO.getEvaluationLevel())) {
            wrapper.eq(SupplierEvaluation::getEvaluationLevel, queryDTO.getEvaluationLevel());
        }
        if (StringUtils.hasText(queryDTO.getEvaluator())) {
            wrapper.like(SupplierEvaluation::getEvaluator, queryDTO.getEvaluator());
        }
        if (queryDTO.getEvaluationDateStart() != null) {
            wrapper.ge(SupplierEvaluation::getEvaluationDate, queryDTO.getEvaluationDateStart());
        }
        if (queryDTO.getEvaluationDateEnd() != null) {
            wrapper.le(SupplierEvaluation::getEvaluationDate, queryDTO.getEvaluationDateEnd());
        }
        if (queryDTO.getTotalScoreMin() != null) {
            wrapper.ge(SupplierEvaluation::getTotalScore, queryDTO.getTotalScoreMin());
        }
        if (queryDTO.getTotalScoreMax() != null) {
            wrapper.le(SupplierEvaluation::getTotalScore, queryDTO.getTotalScoreMax());
        }

        wrapper.orderByDesc(SupplierEvaluation::getEvaluationDate)
               .orderByDesc(SupplierEvaluation::getCreateTime);

        Page<SupplierEvaluation> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        Page<SupplierEvaluation> result = page(page, wrapper);

        return new PageResult<>(page.getCurrent(), page.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public List<SupplierEvaluation> getEvaluationsBySupplierId(Long supplierId) {
        LambdaQueryWrapper<SupplierEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierEvaluation::getSupplierId, supplierId);
        wrapper.orderByDesc(SupplierEvaluation::getEvaluationDate);
        return list(wrapper);
    }

    @Override
    public SupplierEvaluationDTO getLatestEvaluation(Long supplierId) {
        LambdaQueryWrapper<SupplierEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierEvaluation::getSupplierId, supplierId);
        wrapper.orderByDesc(SupplierEvaluation::getEvaluationDate)
               .orderByDesc(SupplierEvaluation::getCreateTime);
        wrapper.last("LIMIT 1");

        SupplierEvaluation evaluation = getOne(wrapper);

        if (evaluation == null) {
            throw new BusinessException("供应商评估不存在");
        }

        SupplierEvaluationDTO dto = new SupplierEvaluationDTO();
        BeanUtils.copyProperties(evaluation, dto);
        return dto;
    }

    @Override
    public BigDecimal calculateTotalScore(BigDecimal qualityScore, BigDecimal deliveryScore,
                                          BigDecimal priceScore, BigDecimal serviceScore) {
        if (qualityScore == null) qualityScore = BigDecimal.ZERO;
        if (deliveryScore == null) deliveryScore = BigDecimal.ZERO;
        if (priceScore == null) priceScore = BigDecimal.ZERO;
        if (serviceScore == null) serviceScore = BigDecimal.ZERO;

        BigDecimal total = qualityScore.multiply(QUALITY_WEIGHT)
                .add(deliveryScore.multiply(DELIVERY_WEIGHT))
                .add(priceScore.multiply(PRICE_WEIGHT))
                .add(serviceScore.multiply(SERVICE_WEIGHT));

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String evaluateLevel(BigDecimal totalScore) {
        if (totalScore == null) {
            return "UNQUALIFIED";
        }

        int score = totalScore.intValue();

        if (score >= 90) {
            return "EXCELLENT";      // 优秀
        } else if (score >= 80) {
            return "GOOD";           // 良好
        } else if (score >= 60) {
            return "QUALIFIED";       // 合格
        } else {
            return "UNQUALIFIED";     // 不合格
        }
    }
}
