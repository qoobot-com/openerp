package com.qoobot.qooerp.monitor.service;

import com.qoobot.qooerp.monitor.dto.*;

import java.util.List;

public interface MonitorAlertChannelService {
    Long createChannel(AlertChannelCreateDTO dto);

    void updateChannel(Long id, AlertChannelUpdateDTO dto);

    void deleteChannel(Long id);

    List<AlertChannelDTO> listChannels();

    AlertChannelDTO getChannel(Long id);
}
