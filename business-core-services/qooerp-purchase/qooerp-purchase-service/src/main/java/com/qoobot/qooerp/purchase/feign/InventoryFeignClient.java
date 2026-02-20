package com.qoobot.qooerp.purchase.feign;

import com.qoobot.qooerp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "qooerp-inventory-service", path = "/api/inventory")
public interface InventoryFeignClient {

    @PostMapping("/increase")
    Result<Void> increaseInventory(@RequestBody Map<String, Object> params);

    @PostMapping("/decrease")
    Result<Void> decreaseInventory(@RequestBody Map<String, Object> params);

    @GetMapping("/query")
    Result<BigDecimal> queryAvailableStock(@RequestParam("materialId") Long materialId,
                                              @RequestParam("warehouseId") Long warehouseId);
}
