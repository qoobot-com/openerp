package com.qoobot.qooerp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.CheckCreateDTO;
import com.qoobot.qooerp.inventory.dto.CheckDTO;
import com.qoobot.qooerp.inventory.dto.CheckQueryDTO;

public interface InventoryCheckService {
    Long createCheck(CheckCreateDTO dto);
    void submitCheck(Long id);
    void auditCheck(Long id, Boolean approved);
    void executeCheck(Long id);
    CheckDTO getCheckById(Long id);
    Page<CheckDTO> queryChecks(CheckQueryDTO dto);
}
