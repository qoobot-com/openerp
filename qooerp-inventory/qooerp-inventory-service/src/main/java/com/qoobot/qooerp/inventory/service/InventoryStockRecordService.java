package com.qoobot.qooerp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.StockRecordDTO;
import com.qoobot.qooerp.inventory.dto.StockRecordQueryDTO;

public interface InventoryStockRecordService {
    Page<StockRecordDTO> queryStockRecords(StockRecordQueryDTO dto);
    void addStockRecord(String recordType, String businessType, String businessNo,
                       Long warehouseId, Long materialId, String batchNo,
                       Long beforeQuantity, Long changeQuantity, Long afterQuantity,
                       Long operatorId, String operatorName);
}
