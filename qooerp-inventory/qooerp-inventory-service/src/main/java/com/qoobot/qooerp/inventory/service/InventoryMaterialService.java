package com.qoobot.qooerp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.MaterialCreateDTO;
import com.qoobot.qooerp.inventory.dto.MaterialDTO;
import com.qoobot.qooerp.inventory.dto.MaterialQueryDTO;
import com.qoobot.qooerp.inventory.dto.MaterialUpdateDTO;

public interface InventoryMaterialService {
    Long createMaterial(MaterialCreateDTO dto);
    void updateMaterial(MaterialUpdateDTO dto);
    void deleteMaterial(Long id);
    MaterialDTO getMaterialById(Long id);
    Page<MaterialDTO> queryMaterials(MaterialQueryDTO dto);
}
