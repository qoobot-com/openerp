package com.qoobot.qooerp.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件分享实体
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file_share")
public class FileShare extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 分享密码（加密）
     */
    private String sharePassword;

    /**
     * 分享标题
     */
    private String shareName;

    /**
     * 分享描述
     */
    private String shareDesc;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 访问次数
     */
    private Integer visitCount;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 状态（1-有效 2-已取消）
     */
    private Integer status;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建人ID
     */
    private Long creatorId;
}
