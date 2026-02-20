package com.qoobot.qooerp.auth.service.impl;

import com.qoobot.qooerp.auth.constants.AuthConstant;
import com.qoobot.qooerp.auth.dto.LoginRequest;
import com.qoobot.qooerp.auth.dto.LoginResponse;
import com.qoobot.qooerp.auth.dto.ThirdPartyLoginRequest;
import com.qoobot.qooerp.auth.dto.TokenResponse;
import com.qoobot.qooerp.auth.entity.AuthUser;
import com.qoobot.qooerp.auth.entity.AuthThirdParty;
import com.qoobot.qooerp.auth.exception.LoginFailedException;
import com.qoobot.qooerp.auth.mapper.AuthThirdPartyMapper;
import com.qoobot.qooerp.auth.mapper.AuthUserMapper;
import com.qoobot.qooerp.auth.service.AuthService;
import com.qoobot.qooerp.auth.service.LoginLogService;
import com.qoobot.qooerp.auth.service.ThirdPartyLoginService;
import com.qoobot.qooerp.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 第三方登录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThirdPartyLoginServiceImpl implements ThirdPartyLoginService {

    private final AuthUserMapper authUserMapper;
    private final AuthThirdPartyMapper authThirdPartyMapper;
    private final TokenService tokenService;
    private final LoginLogService loginLogService;
    private final AuthService authService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${third-party.wechat.app-id:}")
    private String wechatAppId;

    @Value("${third-party.wechat.app-secret:}")
    private String wechatAppSecret;

    @Value("${third-party.dingtalk.app-id:}")
    private String dingtalkAppId;

    @Value("${third-party.dingtalk.app-secret:}")
    private String dingtalkAppSecret;

    @Value("${third-party.wecom.app-id:}")
    private String wecomAppId;

    @Value("${third-party.wecom.app-secret:}")
    private String wecomAppSecret;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse wechatLogin(String code) {
        try {
            // 获取微信access_token
            String url = String.format(
                    "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    wechatAppId, wechatAppSecret, code
            );
            WechatTokenResponse tokenResponse = restTemplate.getForObject(url, WechatTokenResponse.class);

            if (tokenResponse == null || tokenResponse.getErrcode() != null) {
                throw new LoginFailedException("微信授权失败");
            }

            // 获取用户信息
            String userInfoUrl = String.format(
                    "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s",
                    tokenResponse.getAccess_token(), tokenResponse.getOpenid()
            );
            WechatUserInfo wechatUserInfo = restTemplate.getForObject(userInfoUrl, WechatUserInfo.class);

            if (wechatUserInfo == null) {
                throw new LoginFailedException("获取微信用户信息失败");
            }

            // 查找或创建用户
            AuthUser user = findOrCreateUser(
                    "wechat",
                    wechatUserInfo.getOpenid(),
                    wechatUserInfo.getUnionid(),
                    wechatUserInfo.getNickname(),
                    wechatUserInfo.getHeadimgurl()
            );

            return generateLoginResponse(user, wechatUserInfo.getOpenid());
        } catch (LoginFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new LoginFailedException("微信登录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse dingtalkLogin(String code) {
        try {
            // 获取钉钉access_token
            String url = String.format(
                    "https://api.dingtalk.com/v1.0/oauth2/userAccessToken?appId=%s&appSecret=%s",
                    dingtalkAppId, dingtalkAppSecret
            );
            DingtalkTokenRequest tokenRequest = new DingtalkTokenRequest();
            tokenRequest.setCode(code);
            DingtalkTokenResponse tokenResponse = restTemplate.postForObject(url, tokenRequest, DingtalkTokenResponse.class);

            if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
                throw new LoginFailedException("钉钉授权失败");
            }

            // 获取用户信息
            String userInfoUrl = "https://api.dingtalk.com/v1.0/contact/users/me";
            DingtalkUserInfo dingtalkUserInfo = restTemplate.getForObject(userInfoUrl, DingtalkUserInfo.class);

            if (dingtalkUserInfo == null) {
                throw new LoginFailedException("获取钉钉用户信息失败");
            }

            // 查找或创建用户
            AuthUser user = findOrCreateUser(
                    "dingtalk",
                    dingtalkUserInfo.getOpenId(),
                    dingtalkUserInfo.getUnionId(),
                    dingtalkUserInfo.getNick(),
                    dingtalkUserInfo.getAvatar()
            );

            return generateLoginResponse(user, dingtalkUserInfo.getOpenId());
        } catch (LoginFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("钉钉登录失败", e);
            throw new LoginFailedException("钉钉登录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse weComLogin(String code) {
        try {
            // 获取企业微信access_token
            String tokenUrl = String.format(
                    "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s",
                    wecomAppId, wecomAppSecret
            );
            WeComTokenResponse tokenResponse = restTemplate.getForObject(tokenUrl, WeComTokenResponse.class);

            if (tokenResponse == null || tokenResponse.getErrcode() != 0) {
                throw new LoginFailedException("企业微信授权失败");
            }

            // 获取用户信息
            String userInfoUrl = String.format(
                    "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=%s&code=%s",
                    tokenResponse.getAccess_token(), code
            );
            WeComUserInfo weComUserInfo = restTemplate.getForObject(userInfoUrl, WeComUserInfo.class);

            if (weComUserInfo == null || weComUserInfo.getErrcode() != 0) {
                throw new LoginFailedException("获取企业微信用户信息失败");
            }

            // 查找或创建用户
            AuthUser user = findOrCreateUser(
                    "wecom",
                    weComUserInfo.getUserId(),
                    null,
                    weComUserInfo.getName(),
                    null
            );

            return generateLoginResponse(user, weComUserInfo.getUserId());
        } catch (LoginFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("企业微信登录失败", e);
            throw new LoginFailedException("企业微信登录失败");
        }
    }

    @Override
    public LoginResponse oauth2Login(ThirdPartyLoginRequest request) {
        switch (request.getPlatform()) {
            case "wechat":
                return wechatLogin(request.getCode());
            case "dingtalk":
                return dingtalkLogin(request.getCode());
            case "wecom":
                return weComLogin(request.getCode());
            default:
                throw new LoginFailedException("不支持的平台类型");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindThirdParty(Long userId, String platform, String openid, String unionid) {
        // 查找用户
        AuthUser user = authUserMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        // 检查是否已绑定
        AuthThirdParty existing = authThirdPartyMapper.selectByPlatformAndOpenid(platform, openid);
        if (existing != null && !existing.getUserId().equals(userId)) {
            throw new LoginFailedException("该第三方账号已被绑定");
        }

        // 绑定
        if (existing == null) {
            existing = new AuthThirdParty();
            existing.setUserId(userId);
            existing.setPlatform(platform);
            existing.setOpenid(openid);
            existing.setUnionid(unionid);
            existing.setCreateTime(LocalDateTime.now());
            authThirdPartyMapper.insert(existing);
        }

        log.info("用户绑定第三方账号成功，userId: {}, platform: {}", userId, platform);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindThirdParty(Long userId, String platform) {
        authThirdPartyMapper.deleteByUserIdAndPlatform(userId, platform);
        log.info("用户解绑第三方账号成功，userId: {}, platform: {}", userId, platform);
        return true;
    }

    /**
     * 查找或创建用户
     */
    private AuthUser findOrCreateUser(String platform, String openid, String unionid,
                                        String nickname, String avatar) {
        // 先通过openid查找
        AuthThirdParty thirdParty = authThirdPartyMapper.selectByPlatformAndOpenid(platform, openid);

        AuthUser user;
        if (thirdParty != null) {
            // 已存在关联
            user = authUserMapper.selectById(thirdParty.getUserId());
            if (user != null && user.getDeleted() == 0 && user.getStatus() == AuthConstant.USER_STATUS_ENABLED) {
                return user;
            }
        }

        // 创建新用户
        user = new AuthUser();
        user.setTenantId(0L);
        user.setUsername(generateUsername(platform));
        user.setPassword(""); // 第三方登录无需密码
        user.setNickname(nickname != null ? nickname : platform + "用户");
        user.setRealName(nickname);
        user.setAvatar(avatar);
        user.setAccountType(1); // 普通用户
        user.setStatus(AuthConstant.USER_STATUS_ENABLED);
        user.setMfaEnabled(AuthConstant.MFA_DISABLED);
        user.setLoginFailCount(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        authUserMapper.insert(user);

        // 创建关联
        AuthThirdParty newThirdParty = new AuthThirdParty();
        newThirdParty.setUserId(user.getId());
        newThirdParty.setPlatform(platform);
        newThirdParty.setOpenid(openid);
        newThirdParty.setUnionid(unionid);
        newThirdParty.setCreateTime(LocalDateTime.now());
        authThirdPartyMapper.insert(newThirdParty);

        return user;
    }

    /**
     * 生成用户名
     */
    private String generateUsername(String platform) {
        return platform + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 生成登录响应
     */
    private LoginResponse generateLoginResponse(AuthUser user, String openid) {
        // 生成令牌
        TokenResponse tokenResponse = tokenService.generateTokenPair(user);

        // 更新最后登录信息
        user.setLastLoginTime(LocalDateTime.now());
        authUserMapper.updateById(user);

        // 记录成功日志
        loginLogService.recordSuccessLogin(user.getUsername(), user.getId(), "0.0.0.0", "第三方登录");

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .tokenType(tokenResponse.getTokenType())
                .expiresIn(tokenResponse.getExpiresIn())
                .build();
    }

    // 微信响应类
    private static class WechatTokenResponse {
        private String access_token;
        private Long expires_in;
        private String refresh_token;
        private String openid;
        private String scope;
        private String unionid;
        private Integer errcode;
        private String errmsg;

        // getters and setters
        public String getAccess_token() { return access_token; }
        public String getOpenid() { return openid; }
        public String getUnionid() { return unionid; }
        public Integer getErrcode() { return errcode; }
    }

    private static class WechatUserInfo {
        private String openid;
        private String nickname;
        private Integer sex;
        private String province;
        private String city;
        private String country;
        private String headimgurl;
        private String privilege;
        private String unionid;

        // getters and setters
        public String getOpenid() { return openid; }
        public String getUnionid() { return unionid; }
        public String getNickname() { return nickname; }
        public String getHeadimgurl() { return headimgurl; }
    }

    // 钉钉响应类
    private static class DingtalkTokenRequest {
        private String code;

        // getter and setter
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    private static class DingtalkTokenResponse {
        private String accessToken;

        // getter
        public String getAccessToken() { return accessToken; }
    }

    private static class DingtalkUserInfo {
        private String openId;
        private String unionId;
        private String nick;
        private String avatar;

        // getters
        public String getOpenId() { return openId; }
        public String getUnionId() { return unionId; }
        public String getNick() { return nick; }
        public String getAvatar() { return avatar; }
    }

    // 企业微信响应类
    private static class WeComTokenResponse {
        private Integer errcode;
        private String errmsg;
        private String access_token;

        // getters
        public Integer getErrcode() { return errcode; }
        public String getAccess_token() { return access_token; }
    }

    private static class WeComUserInfo {
        private Integer errcode;
        private String errmsg;
        private String userid;
        private String name;

        // getters
        public Integer getErrcode() { return errcode; }
        public String getUserId() { return userid; }
        public String getName() { return name; }
    }
}
