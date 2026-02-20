package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.system.dto.SystemAnnouncementQueryDTO;
import com.qoobot.qooerp.system.entity.SystemAnnouncement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统公告Mapper
 */
@Mapper
public interface SystemAnnouncementMapper extends BaseMapper<SystemAnnouncement> {

    /**
     * 分页查询公告
     */
    IPage<SystemAnnouncement> selectPageByQuery(Page<SystemAnnouncement> page, @Param("query") SystemAnnouncementQueryDTO query);
}
