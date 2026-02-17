package com.qoobot.qooerp.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 第三方登录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_third_party")
public class AuthThirdParty extends BaseEntity {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 平台类型：wechat-微信 dingtalk-钉钉 wecom-企业微信
     */
    private String platform;

    /**
     * 第三方OpenID
     */
    private String openid;

    /**
     * 第三方UnionID
     */
    private String unionid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 绑定时间
     */
    private LocalDateTime bindTime;
}
