package com.qoobot.qooerp.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_statistics")
public class MessageStatistics {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("stat_date")
    private LocalDate statDate;

    @TableField("stat_type")
    private String statType;

    @TableField("message_type")
    private Integer messageType;

    @TableField("channel_code")
    private String channelCode;

    @TableField("total_count")
    private Long totalCount;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
