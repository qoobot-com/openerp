package com.qoobot.qooerp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.inventory.dto.StockRecordDTO;
import com.qoobot.qooerp.inventory.dto.StockRecordQueryDTO;
import com.qoobot.qooerp.inventory.service.InventoryStockRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/record")
@RequiredArgsConstructor
public class InventoryRecordController {

    private final InventoryStockRecordService recordService;

    @GetMapping("/page")
    public Result<Page<StockRecordDTO>> queryStockRecords(StockRecordQueryDTO dto) {
        Page<StockRecordDTO> page = recordService.queryStockRecords(dto);
        return Result.success(page);
    }
}
