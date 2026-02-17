package com.qoobot.qooerp.finance.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.finance.asset.domain.FinanceAsset;
import com.qoobot.qooerp.finance.asset.domain.FinanceAssetDepreciation;
import com.qoobot.qooerp.finance.asset.dto.FinanceAssetDTO;
import com.qoobot.qooerp.finance.asset.dto.AssetQueryDTO;

import java.util.List;

/**
 * 固定资产服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IFinanceAssetService extends IService<FinanceAsset> {

    /**
     * 分页查询资产列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<FinanceAsset> queryPage(AssetQueryDTO queryDTO);

    /**
     * 创建资产
     *
     * @param dto 资产信息
     * @return 资产ID
     */
    Long create(FinanceAssetDTO dto);

    /**
     * 更新资产
     *
     * @param dto 资产信息
     * @return 是否成功
     */
    Boolean update(FinanceAssetDTO dto);

    /**
     * 删除资产
     *
     * @param id 资产ID
     * @return 是否成功
     */
    Boolean delete(Long id);

    /**
     * 资产调拨
     *
     * @param id 资产ID
     * @param departmentId 新部门ID
     * @param departmentName 新部门名称
     * @param userId 新使用人ID
     * @param userName 新使用人姓名
     * @param location 新存放地点
     * @return 是否成功
     */
    Boolean transfer(Long id, Long departmentId, String departmentName, Long userId, String userName, String location);

    /**
     * 资产报废
     *
     * @param id 资产ID
     * @return 是否成功
     */
    Boolean scrap(Long id);

    /**
     * 计算折旧
     *
     * @param assetId 资产ID
     * @param period 折旧期间
     * @return 折旧记录
     */
    FinanceAssetDepreciation calculateDepreciation(Long assetId, String period);

    /**
     * 批量计提折旧
     *
     * @param period 折旧期间
     * @return 计提折旧的资产数
     */
    Integer batchCalculateDepreciation(String period);

    /**
     * 获取资产折旧历史
     *
     * @param assetId 资产ID
     * @return 折旧历史列表
     */
    List<FinanceAssetDepreciation> getDepreciationHistory(Long assetId);
}
