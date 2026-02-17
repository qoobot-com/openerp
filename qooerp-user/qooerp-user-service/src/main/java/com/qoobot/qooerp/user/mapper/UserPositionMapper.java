package com.qoobot.qooerp.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.user.entity.UserPosition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户岗位关联Mapper
 */
@Mapper
public interface UserPositionMapper extends BaseMapper<UserPosition> {

    /**
     * 查询用户岗位列表
     */
    List<Long> selectPositionIdsByUserId(@Param("userId") Long userId);

    /**
     * 删除用户岗位关联
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除岗位用户关联
     */
    int deleteByPositionId(@Param("positionId") Long positionId);
}
