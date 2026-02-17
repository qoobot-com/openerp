package com.qoobot.qooerp.scm.supplier.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.supplier.domain.Supplier;
import com.qoobot.qooerp.scm.supplier.dto.SupplierDTO;
import com.qoobot.qooerp.scm.supplier.dto.SupplierFilterDTO;
import com.qoobot.qooerp.scm.supplier.dto.SupplierQueryDTO;
import com.qoobot.qooerp.scm.supplier.dto.SupplierWithEvaluationDTO;

import java.util.List;

/**
 * 供应商Service接口
 *
 * @author QooERP
 * @since 2026-02-17
 */
public interface ISupplierService extends IService<Supplier> {

    /**
     * 创建供应商
     *
     * @param dto 供应商DTO
     * @return 供应商ID
     */
    Long create(SupplierDTO dto);

    /**
     * 更新供应商
     *
     * @param dto 供应商DTO
     * @return 是否成功
     */
    boolean update(SupplierDTO dto);

    /**
     * 删除供应商
     *
     * @param id 供应商ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 查询供应商详情
     *
     * @param id 供应商ID
     * @return 供应商DTO
     */
    SupplierDTO getDetail(Long id);

    /**
     * 分页查询供应商列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<Supplier> queryPage(SupplierQueryDTO queryDTO);

    /**
     * 根据编码查询供应商
     *
     * @param supplierCode 供应商编码
     * @return 供应商
     */
    Supplier getByCode(String supplierCode);

    /**
     * 更新供应商状态
     *
     * @param id 供应商ID
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

    /**
     * 多条件筛选供应商（含评估信息）
     *
     * @param filterDTO 筛选条件
     * @return 分页结果
     */
    PageResult<SupplierWithEvaluationDTO> filterSuppliers(SupplierFilterDTO filterDTO);
}
