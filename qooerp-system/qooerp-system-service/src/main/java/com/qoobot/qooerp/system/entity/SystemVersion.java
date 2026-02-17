package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 版本管理实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_version")
public class SystemVersion extends BaseEntity {

    /**
     * 版本ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 版本号
     */
    private String version;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 版本类型：MAJOR-主版本 MINOR-次版本 PATCH-补丁
     */
    private String versionType;

    /**
     * 版本状态：DRAFT-草稿 TESTING-测试中 PUBLISHED-已发布 ROLLED_BACK-已回滚
     */
    private String status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 回滚时间
     */
    private LocalDateTime rollbackTime;

    /**
     * 版本描述
     */
    private String description;

    /**
     * 变更内容（Markdown格式）
     */
    private String changeLog;

    /**
     * 升级脚本路径
     */
    private String upgradeScript;

    /**
     * 回滚脚本路径
     */
    private String rollbackScript;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 文件校验值
     */
    private String checksum;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 是否强制升级
     */
    private Boolean forceUpgrade;

    /**
     * 灰度发布实例数
     */
    private Integer canaryInstances;

    /**
     * 灰度发布状态：NOT_STARTED-未开始 ROLLING_OUT-灰度中 ROLLED_OUT-已完成
     */
    private String canaryStatus;
}
