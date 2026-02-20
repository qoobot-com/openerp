package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.entity.ProductionEquipment;
import com.qoobot.qooerp.production.vo.ProductionEquipmentVO;

/**
 * 生产设备Service接口
 */
public interface ProductionEquipmentService extends IService<ProductionEquipment> {
    Long createProductionEquipment(ProductionEquipment equipment);
    Boolean updateProductionEquipment(Long id, ProductionEquipment equipment);
    Boolean deleteProductionEquipment(Long id);
    ProductionEquipment getProductionEquipmentById(Long id);
    IPage<ProductionEquipmentVO> queryProductionEquipmentPage(Integer current, Integer size, String equipmentCode, String equipmentName, String status, Long workshopId);
}
