package com.qoobot.qooerp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.WarehouseCreateDTO;
import com.qoobot.qooerp.inventory.dto.WarehouseDTO;
import com.qoobot.qooerp.inventory.dto.WarehouseQueryDTO;
import com.qoobot.qooerp.inventory.dto.WarehouseUpdateDTO;

import java.util.List;

public interface InventoryWarehouseService {
    Long createWarehouse(WarehouseCreateDTO dto);
    void updateWarehouse(WarehouseUpdateDTO dto);
    void deleteWarehouse(Long id);
    WarehouseDTO getWarehouseById(Long id);
    Page<WarehouseDTO> queryWarehouses(WarehouseQueryDTO dto);
    List<WarehouseDTO> getAllWarehouses();
}
