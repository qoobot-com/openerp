package com.qoobot.qooerp.notify.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.notify.dto.NotifyRecordQueryDTO;
import com.qoobot.qooerp.notify.dto.NotifyRecordDTO;
import com.qoobot.qooerp.notify.dto.TemplateCreateDTO;
import com.qoobot.qooerp.notify.dto.TemplateDTO;
import com.qoobot.qooerp.notify.dto.TemplatePreviewDTO;
import com.qoobot.qooerp.notify.dto.TemplateQueryDTO;
import com.qoobot.qooerp.notify.dto.TemplateUpdateDTO;

/**
 * 模板服务接口
 */
public interface TemplateService {

    /**
     * 创建模板
     */
    Long create(TemplateCreateDTO dto);

    /**
     * 更新模板
     */
    void update(TemplateUpdateDTO dto);

    /**
     * 删除模板
     */
    void delete(Long id);

    /**
     * 获取模板详情
     */
    TemplateDTO get(Long id);

    /**
     * 查询模板分页
     */
    Page<TemplateDTO> page(TemplateQueryDTO dto);

    /**
     * 模板预览
     */
    Map<String, Object> preview(TemplatePreviewDTO dto);

    /**
     * 查询记录分页
     */
    Page<NotifyRecordDTO> pageRecords(NotifyRecordQueryDTO dto);

    /**
     * 获取记录详情
     */
    NotifyRecordDTO getRecord(Long id);
}
