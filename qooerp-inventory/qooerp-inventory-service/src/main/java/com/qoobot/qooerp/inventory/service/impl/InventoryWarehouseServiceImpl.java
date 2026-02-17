package com.qoobot.qooerp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.WarehouseCreateDTO;
import com.qoobot.qooerp.inventory.dto.WarehouseDTO;
import com.qoobot.qooerp.inventory.dto.WarehouseQueryDTO;
import com.qoobot.qooerp.inventory.dto.WarehouseUpdateDTO;
import com.qoobot.qooerp.inventory.entity.InventoryWarehouse;
import com.qoobot.qooerp.inventory.mapper.InventoryWarehouseMapper;
import com.qoobot.qooerp.inventory.service.InventoryWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryWarehouseServiceImpl implements InventoryWarehouseService {

    private final InventoryWarehouseMapper warehouseMapper;

    @Override
    public Long createWarehouse(WarehouseCreateDTO dto) {
        InventoryWarehouse warehouse = new InventoryWarehouse();
        BeanUtils.copyProperties(dto, warehouse);
        warehouse.setTenantId(getCurrentTenantId());
        warehouse.setWarehouseStatus("ENABLED");
        warehouse.setCreateTime(LocalDateTime.now());
        warehouseMapper.insert(warehouse);
        return warehouse.getId();
    }

    @Override
    public void updateWarehouse(WarehouseUpdateDTO dto) {
        InventoryWarehouse warehouse = warehouseMapper.selectById(dto.getId());
        if (warehouse == null) {
            throw new RuntimeException("仓库不存在");
        }
        BeanUtils.copyProperties(dto, warehouse);
        warehouse.setUpdateTime(LocalDateTime.now());
        warehouseMapper.updateById(warehouse);
    }

    @Override
    public void deleteWarehouse(Long id) {
        warehouseMapper.deleteById(id);
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id) {
        InventoryWarehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new RuntimeException("仓库不存在");
        }
        return convertToDTO(warehouse);
    }

    @Override
    public Page<WarehouseDTO> queryWarehouses(WarehouseQueryDTO dto) {
        IPage<InventoryWarehouse> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<InventoryWarehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryWarehouse::getTenantId, getCurrentTenantId())
                .like(dto.getWarehouseCode() != null, InventoryWarehouse::getWarehouseCode, dto.getWarehouseCode())
                .like(dto.getWarehouseName() != null, InventoryWarehouse::getWarehouseName, dto.getWarehouseName())
                .eq(dto.getWarehouseType() != null, InventoryWarehouse::getWarehouseType, dto.getWarehouseType())
                .eq(dto.getWarehouseStatus() != null, InventoryWarehouse::getWarehouseStatus, dto.getWarehouseStatus());
        IPage<InventoryWarehouse> result = warehouseMapper.selectPage(page, wrapper);

        Page<WarehouseDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public List<WarehouseDTO> getAllWarehouses() {
        LambdaQueryWrapper<InventoryWarehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryWarehouse::getTenantId, getCurrentTenantId())
                .eq(InventoryWarehouse::getWarehouseStatus, "ENABLED");
        return warehouseMapper.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private WarehouseDTO convertToDTO(InventoryWarehouse entity) {
        WarehouseDTO dto = new WarehouseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private Long getCurrentTenantId() {
        return 1L;
    }
}
