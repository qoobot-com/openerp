package com.qoobot.qooerp.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.dto.PageQueryDTO;
import com.qoobot.qooerp.crm.dto.CrmServiceDTO;

/**
 * 客户服务接口
 */
public interface CrmCustomerServiceService {

    /**
     * 创建服务请求
     */
    CrmServiceDTO createService(CrmServiceDTO dto);

    /**
     * 更新服务
     */
    CrmServiceDTO updateService(Long id, CrmServiceDTO dto);

    /**
     * 获取服务详情
     */
    CrmServiceDTO getService(Long id);

    /**
     * 分页查询服务
     */
    IPage<CrmServiceDTO> listServices(PageQueryDTO pageParam, Long customerId, Integer status, Long assigneeId);

    /**
     * 删除服务
     */
    void deleteService(Long id);

    /**
     * 分配服务
     */
    void assignService(Long id, Long assigneeId, String assigneeName);

    /**
     * 关闭服务
     */
    void closeService(Long id);
}
