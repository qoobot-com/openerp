package com.qoobot.qooerp.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.monitor.dto.*;

public interface MonitorAlertService {
    Page<MonitorAlertDTO> listAlerts(AlertQueryDTO dto);

    MonitorAlertDTO getAlert(Long id);

    void handleAlert(Long id, AlertHandleDTO dto);

    AlertStatisticsDTO getStatistics(AlertStatisticsQueryDTO dto);
}
