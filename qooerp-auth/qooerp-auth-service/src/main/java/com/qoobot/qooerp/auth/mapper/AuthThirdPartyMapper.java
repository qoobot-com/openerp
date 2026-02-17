package com.qoobot.qooerp.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.auth.entity.AuthThirdParty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 第三方登录Mapper
 */
@Mapper
public interface AuthThirdPartyMapper extends BaseMapper<AuthThirdParty> {

    /**
     * 根据平台和OpenID查询
     */
    @Select("SELECT * FROM auth_third_party WHERE platform = #{platform} AND openid = #{openid} AND deleted = 0")
    AuthThirdParty selectByPlatformAndOpenid(@Param("platform") String platform, @Param("openid") String openid);

    /**
     * 根据用户ID和平台删除
     */
    void deleteByUserIdAndPlatform(@Param("userId") Long userId, @Param("platform") String platform);
}
