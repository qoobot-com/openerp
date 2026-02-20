package com.qoobot.qooerp.purchase.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.purchase.dto.ReceiptCreateDTO;
import com.qoobot.qooerp.purchase.dto.ReceiptDTO;
import com.qoobot.qooerp.purchase.dto.ReceiptQueryDTO;

public interface PurchaseReceiptService {

    Result<Long> createReceipt(ReceiptCreateDTO dto);

    Result<Void> deleteReceipt(Long id);

    Result<Void> approveReceipt(Long id);

    Result<Void> cancelReceipt(Long id);

    Result<ReceiptDTO> getReceipt(Long id);

    Result<PageResult<ReceiptDTO>> queryPage(ReceiptQueryDTO dto);
}
