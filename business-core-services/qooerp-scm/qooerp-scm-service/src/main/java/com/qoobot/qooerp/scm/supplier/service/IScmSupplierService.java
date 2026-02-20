package com.qoobot.qooerp.scm.supplier.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.supplier.domain.ScmSupplier;
import com.qoobot.qooerp.scm.supplier.domain.ScmSupplierEvaluation;
import com.qoobot.qooerp.scm.supplier.dto.EvaluationDTO;
import com.qoobot.qooerp.scm.supplier.dto.SupplierDTO;
import com.qoobot.qooerp.scm.supplier.dto.SupplierQueryDTO;

import java.util.List;

/**
 * 供应商Service接口
 *
 * @author QooERP
 * @since 2026-02-18
 */
public interface IScmSupplierService extends IService<ScmSupplier> {

    /**
     * 创建供应商
     *
     * @param dto 供应商DTO
     * @return 供应商ID
     */
    Long createSupplier(SupplierDTO dto);

    /**
     * 更新供应商
     *
     * @param id  供应商ID
     * @param dto 供应商DTO
     * @return 是否成功
     */
    boolean updateSupplier(Long id, SupplierDTO dto);

    /**
     * 删除供应商
     *
     * @param id 供应商ID
     * @return 是否成功
     */
    boolean deleteSupplier(Long id);

    /**
     * 查询供应商详情
     *
     * @param id 供应商ID
     * @return 供应商DTO
     */
    SupplierDTO getSupplier(Long id);

    /**
     * 分页查询供应商列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<ScmSupplier> querySuppliers(SupplierQueryDTO queryDTO);

    /**
     * 评估供应商
     *
     * @param supplierId 供应商ID
     * @param dto        评估DTO
     * @return 是否成功
     */
    boolean evaluateSupplier(Long supplierId, EvaluationDTO dto);

    /**
     * 获取评估历史
     *
     * @param supplierId 供应商ID
     * @return 评估历史列表
     */
    List<ScmSupplierEvaluation> getEvaluationHistory(Long supplierId);

    /**
     * 更新供应商状态
     *
     * @param id     供应商ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, String status);

    /**
     * 生成供应商编码
     *
     * @return 供应商编码
     */
    String generateSupplierCode();
}
