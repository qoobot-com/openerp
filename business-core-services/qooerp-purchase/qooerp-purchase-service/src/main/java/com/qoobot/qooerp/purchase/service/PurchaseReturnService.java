package com.qoobot.qooerp.purchase.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.purchase.dto.ReturnCreateDTO;
import com.qoobot.qooerp.purchase.dto.ReturnDTO;
import com.qoobot.qooerp.purchase.dto.ReturnQueryDTO;

public interface PurchaseReturnService {

    Result<Long> createReturn(ReturnCreateDTO dto);

    Result<Void> deleteReturn(Long id);

    Result<Void> approveReturn(Long id);

    Result<Void> completeReturn(Long id);

    Result<Void> cancelReturn(Long id);

    Result<ReturnDTO> getReturn(Long id);

    Result<PageResult<ReturnDTO>> queryPage(ReturnQueryDTO dto);
}
