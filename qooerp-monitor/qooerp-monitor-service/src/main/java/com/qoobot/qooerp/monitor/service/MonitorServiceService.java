package com.qoobot.qooerp.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.monitor.dto.*;

import java.util.List;

public interface MonitorServiceService {
    Long registerService(MonitorServiceCreateDTO dto);

    void updateService(Long id, MonitorServiceUpdateDTO dto);

    void deleteService(Long id);

    MonitorServiceDTO getService(Long id);

    List<MonitorServiceDTO> listServices();

    void healthCheck(ServiceCheckDTO dto);
}
