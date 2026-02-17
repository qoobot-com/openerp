package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.entity.ProductionScrap;
import com.qoobot.qooerp.production.vo.ProductionScrapVO;

/**
 * 生产报废Service接口
 */
public interface ProductionScrapService extends IService<ProductionScrap> {
    Long createProductionScrap(ProductionScrap scrap);
    Boolean updateProductionScrap(Long id, ProductionScrap scrap);
    Boolean deleteProductionScrap(Long id);
    ProductionScrap getProductionScrapById(Long id);
    IPage<ProductionScrapVO> queryProductionScrapPage(Integer current, Integer size, String scrapNo, String status);
    Boolean approveScrap(Long id);
}
