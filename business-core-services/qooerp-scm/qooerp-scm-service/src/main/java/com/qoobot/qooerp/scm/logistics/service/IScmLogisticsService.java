package com.qoobot.qooerp.scm.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.logistics.domain.ScmLogistics;
import com.qoobot.qooerp.scm.logistics.domain.ScmLogisticsTracking;
import com.qoobot.qooerp.scm.logistics.dto.LogisticsDTO;
import com.qoobot.qooerp.scm.logistics.dto.LogisticsQueryDTO;

import java.util.List;

/**
 * 物流Service接口
 *
 * @author QooERP
 * @since 2026-02-18
 */
public interface IScmLogisticsService extends IService<ScmLogisticsTracking> {

    Long createLogistics(LogisticsDTO dto);

    boolean updateLogistics(Long id, LogisticsDTO dto);

    boolean deleteLogistics(Long id);

    LogisticsDTO getLogistics(Long id);

    PageResult<ScmLogistics> queryLogistics(LogisticsQueryDTO queryDTO);

    boolean trackLogistics(Long logisticsId, String status, String location, String description);

    List<ScmLogisticsTracking> queryTracking(Long logisticsId);

    boolean updateStatus(Long id, String status);

    String generateLogisticsCode();
}
