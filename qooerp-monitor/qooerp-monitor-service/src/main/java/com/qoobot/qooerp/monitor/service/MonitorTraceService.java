package com.qoobot.qooerp.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.monitor.dto.*;

import java.util.List;

public interface MonitorTraceService {
    List<MonitorTraceDTO> getTrace(String traceId);

    Page<MonitorTraceDTO> listTraces(TraceQueryDTO dto);

    List<MonitorTraceDTO> getSpans(String traceId);
}
