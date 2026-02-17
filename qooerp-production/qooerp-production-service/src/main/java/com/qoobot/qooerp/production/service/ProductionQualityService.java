package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.dto.ProductionQualityDTO;
import com.qoobot.qooerp.production.entity.ProductionQuality;
import com.qoobot.qooerp.production.vo.ProductionQualityVO;

import java.util.List;

/**
 * 生产质检Service接口
 */
public interface ProductionQualityService extends IService<ProductionQuality> {

    Long createProductionQuality(ProductionQualityDTO dto);
    Boolean updateProductionQuality(Long id, ProductionQualityDTO dto);
    Boolean deleteProductionQuality(Long id);
    ProductionQualityVO getProductionQualityById(Long id);
    IPage<ProductionQualityVO> queryProductionQualityPage(Integer current, Integer size, String qualityNo, String status);
    Boolean batchDeleteProductionQuality(List<Long> ids);
    Boolean startInspection(Long id);
    Boolean passQualityCheck(Long id);
    Boolean failQualityCheck(Long id);
}
