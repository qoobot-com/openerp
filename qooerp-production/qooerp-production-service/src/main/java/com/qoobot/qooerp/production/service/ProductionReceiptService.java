package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.dto.ProductionReceiptDTO;
import com.qoobot.qooerp.production.entity.ProductionReceipt;
import com.qoobot.qooerp.production.vo.ProductionReceiptVO;

import java.util.List;

/**
 * 生产入库Service接口
 */
public interface ProductionReceiptService extends IService<ProductionReceipt> {

    Long createProductionReceipt(ProductionReceiptDTO dto);
    Boolean updateProductionReceipt(Long id, ProductionReceiptDTO dto);
    Boolean deleteProductionReceipt(Long id);
    ProductionReceiptVO getProductionReceiptById(Long id);
    IPage<ProductionReceiptVO> queryProductionReceiptPage(Integer current, Integer size, String receiptNo, String status);
    Boolean batchDeleteProductionReceipt(List<Long> ids);
    Boolean approveProductionReceipt(Long id);
    Boolean warehouseReceipt(Long id);
}
