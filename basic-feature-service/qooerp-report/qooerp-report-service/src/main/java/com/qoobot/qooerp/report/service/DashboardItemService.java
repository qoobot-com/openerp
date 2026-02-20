package com.qoobot.qooerp.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.report.dto.DashboardItemCreateDTO;
import com.qoobot.qooerp.report.dto.DashboardItemDTO;
import com.qoobot.qooerp.report.dto.DashboardItemQueryDTO;
import com.qoobot.qooerp.report.dto.DashboardItemUpdateDTO;
import com.qoobot.qooerp.report.entity.DashboardItem;

import java.util.List;

public interface DashboardItemService extends IService<DashboardItem> {
    
    DashboardItemDTO create(DashboardItemCreateDTO dto);
    
    DashboardItemDTO update(Long id, DashboardItemUpdateDTO dto);
    
    void delete(Long id);
    
    DashboardItemDTO getById(Long id);
    
    PageResult<DashboardItemDTO> queryPage(DashboardItemQueryDTO dto);
    
    List<DashboardItemDTO> getByDashboardId(Long dashboardId);
    
    void updateSort(Long id, Integer sortOrder);
}
