package com.qoobot.qooerp.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.report.dto.ReportHistoryDTO;
import com.qoobot.qooerp.report.dto.ReportHistoryQueryDTO;
import com.qoobot.qooerp.report.entity.ReportHistory;

public interface ReportHistoryService extends IService<ReportHistory> {
    
    ReportHistoryDTO createSnapshot(Long reportId, String snapshotData, String snapshotConfig);
    
    PageResult<ReportHistoryDTO> queryPage(ReportHistoryQueryDTO dto);
    
    ReportHistoryDTO getById(Long id);
    
    void delete(Long id);
    
    void deleteByReportId(Long reportId);
}
