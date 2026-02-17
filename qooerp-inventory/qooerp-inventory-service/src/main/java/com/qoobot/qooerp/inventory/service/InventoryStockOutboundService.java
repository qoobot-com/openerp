package com.qoobot.qooerp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.OutboundCreateDTO;
import com.qoobot.qooerp.inventory.dto.OutboundDTO;
import com.qoobot.qooerp.inventory.dto.OutboundQueryDTO;

public interface InventoryStockOutboundService {
    Long createOutbound(OutboundCreateDTO dto);
    void submitOutbound(Long id);
    void auditOutbound(Long id, Boolean approved);
    void executeOutbound(Long id);
    OutboundDTO getOutboundById(Long id);
    Page<OutboundDTO> queryOutbounds(OutboundQueryDTO dto);
}
