package com.qoobot.qooerp.message.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.MessageChannelCreateDTO;
import com.qoobot.qooerp.message.dto.MessageChannelDTO;
import com.qoobot.qooerp.message.dto.MessageChannelQueryDTO;

public interface MessageChannelService {
    Long createChannel(MessageChannelCreateDTO dto);
    void updateChannel(Long id, MessageChannelCreateDTO dto);
    void deleteChannel(Long id);
    void enableChannel(Long id);
    void disableChannel(Long id);
    MessageChannelDTO getChannelById(Long id);
    Page<MessageChannelDTO> queryChannels(MessageChannelQueryDTO dto);
}
