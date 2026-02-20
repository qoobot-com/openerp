package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchMarkReadDTO {
    private List<Long> messageIds;
}
