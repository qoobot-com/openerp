package com.qoobot.qooerp.purchase.feign;

import com.qoobot.qooerp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "qooerp-inventory-service", path = "/api/material")
public interface MaterialFeignClient {

    @GetMapping("/{id}")
    Result<Map<String, Object>> getMaterial(@PathVariable("id") Long id);

    @PostMapping("/batch")
    Result<List<Map<String, Object>>> batchGetMaterials(@RequestBody List<Long> ids);
}
