package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.dto.ProductionMaterialDTO;
import com.qoobot.qooerp.production.entity.ProductionMaterial;
import com.qoobot.qooerp.production.vo.ProductionMaterialVO;

import java.util.List;

/**
 * 生产领料Service接口
 */
public interface ProductionMaterialService extends IService<ProductionMaterial> {

    Long createProductionMaterial(ProductionMaterialDTO dto);
    Boolean updateProductionMaterial(Long id, ProductionMaterialDTO dto);
    Boolean deleteProductionMaterial(Long id);
    ProductionMaterialVO getProductionMaterialById(Long id);
    IPage<ProductionMaterialVO> queryProductionMaterialPage(Integer current, Integer size, String materialNo, String status);
    Boolean batchDeleteProductionMaterial(List<Long> ids);
    Boolean approveProductionMaterial(Long id);
    Boolean issueMaterial(Long id);
}
