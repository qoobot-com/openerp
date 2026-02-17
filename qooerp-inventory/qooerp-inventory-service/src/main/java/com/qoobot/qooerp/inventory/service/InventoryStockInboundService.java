package com.qoobot.qooerp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.InboundCreateDTO;
import com.qoobot.qooerp.inventory.dto.InboundDTO;
import com.qoobot.qooerp.inventory.dto.InboundQueryDTO;

public interface InventoryStockInboundService {
    Long createInbound(InboundCreateDTO dto);
    void submitInbound(Long id);
    void auditInbound(Long id, Boolean approved);
    void executeInbound(Long id);
    InboundDTO getInboundById(Long id);
    Page<InboundDTO> queryInbounds(InboundQueryDTO dto);
}
