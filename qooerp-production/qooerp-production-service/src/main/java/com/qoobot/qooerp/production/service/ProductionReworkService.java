package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.entity.ProductionRework;
import com.qoobot.qooerp.production.vo.ProductionReworkVO;

/**
 * 生产返工Service接口
 */
public interface ProductionReworkService extends IService<ProductionRework> {
    Long createProductionRework(ProductionRework rework);
    Boolean updateProductionRework(Long id, ProductionRework rework);
    Boolean deleteProductionRework(Long id);
    ProductionRework getProductionReworkById(Long id);
    IPage<ProductionReworkVO> queryProductionReworkPage(Integer current, Integer size, String reworkNo, String status);
    Boolean startRework(Long id);
    Boolean completeRework(Long id);
}
