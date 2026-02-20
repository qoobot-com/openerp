package com.qoobot.qooerp.purchase.feign;

import com.qoobot.qooerp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "qooerp-inventory-service", path = "/api/warehouse")
public interface WarehouseFeignClient {

    @GetMapping("/{id}")
    Result<Map<String, Object>> getWarehouse(@PathVariable("id") Long id);
}
