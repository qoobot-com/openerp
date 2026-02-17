package com.qoobot.qooerp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.TransferCreateDTO;
import com.qoobot.qooerp.inventory.dto.TransferDTO;
import com.qoobot.qooerp.inventory.dto.TransferQueryDTO;

public interface InventoryTransferService {
    Long createTransfer(TransferCreateDTO dto);
    void submitTransfer(Long id);
    void auditTransfer(Long id, Boolean approved);
    void executeTransfer(Long id);
    TransferDTO getTransferById(Long id);
    Page<TransferDTO> queryTransfers(TransferQueryDTO dto);
}
