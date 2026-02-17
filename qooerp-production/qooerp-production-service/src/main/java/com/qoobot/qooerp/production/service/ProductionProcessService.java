package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.entity.ProductionProcess;

/**
 * 生产工序Service接口
 */
public interface ProductionProcessService extends IService<ProductionProcess> {
    Long createProductionProcess(ProductionProcess process);
    Boolean updateProductionProcess(Long id, ProductionProcess process);
    Boolean deleteProductionProcess(Long id);
    ProductionProcess getProductionProcessById(Long id);
}
