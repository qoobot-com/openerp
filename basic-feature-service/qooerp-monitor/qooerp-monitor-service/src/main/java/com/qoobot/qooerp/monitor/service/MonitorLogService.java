package com.qoobot.qooerp.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.monitor.dto.*;

import java.util.List;

public interface MonitorLogService {
    Page<MonitorLogDTO> listLogs(LogQueryDTO dto);

    List<String> listLogTypes();
}
