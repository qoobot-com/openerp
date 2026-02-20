package com.qoobot.qooerp.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.report.dto.ReportSubscriptionCreateDTO;
import com.qoobot.qooerp.report.dto.ReportSubscriptionDTO;
import com.qoobot.qooerp.report.dto.ReportSubscriptionQueryDTO;
import com.qoobot.qooerp.report.dto.ReportSubscriptionUpdateDTO;
import com.qoobot.qooerp.report.entity.ReportSubscription;

public interface ReportSubscriptionService extends IService<ReportSubscription> {
    
    ReportSubscriptionDTO create(ReportSubscriptionCreateDTO dto);
    
    ReportSubscriptionDTO update(Long id, ReportSubscriptionUpdateDTO dto);
    
    void delete(Long id);
    
    ReportSubscriptionDTO getById(Long id);
    
    PageResult<ReportSubscriptionDTO> queryPage(ReportSubscriptionQueryDTO dto);
    
    void pause(Long id);
    
    void resume(Long id);
    
    void executeNow(Long id);
}
