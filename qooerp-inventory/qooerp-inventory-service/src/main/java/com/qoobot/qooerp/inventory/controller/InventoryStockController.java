package com.qoobot.qooerp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.inventory.dto.StockAdjustDTO;
import com.qoobot.qooerp.inventory.dto.StockAlertDTO;
import com.qoobot.qooerp.inventory.dto.StockDTO;
import com.qoobot.qooerp.inventory.dto.StockQueryDTO;
import com.qoobot.qooerp.inventory.service.InventoryStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/stock")
@RequiredArgsConstructor
public class InventoryStockController {

    private final InventoryStockService stockService;

    @GetMapping("/{id}")
    public Result<StockDTO> getStockById(@PathVariable Long id) {
        StockDTO dto = stockService.getStockById(id);
        return Result.success(dto);
    }

    @GetMapping("/page")
    public Result<Page<StockDTO>> queryStocks(StockQueryDTO dto) {
        Page<StockDTO> page = stockService.queryStocks(dto);
        return Result.success(page);
    }

    @PostMapping("/adjust")
    public Result<Void> adjustStock(@Valid @RequestBody StockAdjustDTO dto) {
        stockService.adjustStock(dto);
        return Result.success();
    }

    @GetMapping("/alerts")
    public Result<List<StockAlertDTO>> getStockAlerts() {
        List<StockAlertDTO> alerts = stockService.getStockAlerts();
        return Result.success(alerts);
    }

    @PostMapping("/lock")
    public Result<Void> lockStock(@RequestParam Long stockId, @RequestParam Long quantity) {
        stockService.lockStock(stockId, quantity);
        return Result.success();
    }

    @PostMapping("/unlock")
    public Result<Void> unlockStock(@RequestParam Long stockId, @RequestParam Long quantity) {
        stockService.unlockStock(stockId, quantity);
        return Result.success();
    }
}
