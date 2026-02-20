package com.qoobot.qooerp.message.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.message.dto.MessageReceiverDTO;
import com.qoobot.qooerp.message.dto.MessageReceiverQueryDTO;

public interface MessageReceiverService {
    Page<MessageReceiverDTO> queryReceivers(MessageReceiverQueryDTO dto);
}
