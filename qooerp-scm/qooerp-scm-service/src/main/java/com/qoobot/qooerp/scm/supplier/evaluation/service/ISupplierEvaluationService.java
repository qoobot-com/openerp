package com.qoobot.qooerp.scm.supplier.evaluation.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.supplier.evaluation.domain.SupplierEvaluation;
import com.qoobot.qooerp.scm.supplier.evaluation.dto.EvaluationQueryDTO;
import com.qoobot.qooerp.scm.supplier.evaluation.dto.SupplierEvaluationDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 供应商评估Service接口
 *
 * @author QooERP
 * @since 2026-02-17
 */
public interface ISupplierEvaluationService {

    /**
     * 创建供应商评估
     *
     * @param dto 供应商评估DTO
     * @return 评估ID
     */
    Long create(SupplierEvaluationDTO dto);

    /**
     * 更新供应商评估
     *
     * @param dto 供应商评估DTO
     * @return 是否成功
     */
    boolean update(SupplierEvaluationDTO dto);

    /**
     * 删除供应商评估
     *
     * @param id 评估ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 获取评估详情
     *
     * @param id 评估ID
     * @return 评估DTO
     */
    SupplierEvaluationDTO getDetail(Long id);

    /**
     * 分页查询评估列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<SupplierEvaluation> queryPage(EvaluationQueryDTO queryDTO);

    /**
     * 根据供应商ID获取评估列表
     *
     * @param supplierId 供应商ID
     * @return 评估列表
     */
    List<SupplierEvaluation> getEvaluationsBySupplierId(Long supplierId);

    /**
     * 获取供应商最新评估
     *
     * @param supplierId 供应商ID
     * @return 评估DTO
     */
    SupplierEvaluationDTO getLatestEvaluation(Long supplierId);

    /**
     * 计算综合得分
     *
     * @param qualityScore 质量得分
     * @param deliveryScore 交期得分
     * @param priceScore 价格得分
     * @param serviceScore 服务得分
     * @return 综合得分
     */
    BigDecimal calculateTotalScore(BigDecimal qualityScore, BigDecimal deliveryScore,
                                  BigDecimal priceScore, BigDecimal serviceScore);

    /**
     * 判定评估等级
     *
     * @param totalScore 综合得分
     * @return 评估等级
     */
    String evaluateLevel(BigDecimal totalScore);
}
