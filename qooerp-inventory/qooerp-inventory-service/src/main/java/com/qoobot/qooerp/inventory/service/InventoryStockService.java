package com.qoobot.qooerp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.StockAdjustDTO;
import com.qoobot.qooerp.inventory.dto.StockAlertDTO;
import com.qoobot.qooerp.inventory.dto.StockDTO;
import com.qoobot.qooerp.inventory.dto.StockQueryDTO;

import java.util.List;

public interface InventoryStockService {
    StockDTO getStockById(Long id);
    Page<StockDTO> queryStocks(StockQueryDTO dto);
    void adjustStock(StockAdjustDTO dto);
    List<StockAlertDTO> getStockAlerts();
    void lockStock(Long stockId, Long quantity);
    void unlockStock(Long stockId, Long quantity);
}
