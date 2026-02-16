package com.qoobot.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus自动填充处理器
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        this.strictInsertFill(metaObject, "createdTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updatedTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "deleted", Boolean.class, false);
        this.strictInsertFill(metaObject, "version", Integer.class, 1);

        // TODO: 如果需要填充创建人ID，可以从当前登录用户上下文获取
        // Long userId = SecurityContextHolder.getUserId();
        // this.strictInsertFill(metaObject, "createdBy", Long.class, userId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        this.strictUpdateFill(metaObject, "updatedTime", Date.class, new Date());

        // TODO: 如果需要填充更新人ID，可以从当前登录用户上下文获取
        // Long userId = SecurityContextHolder.getUserId();
        // this.strictUpdateFill(metaObject, "updatedBy", Long.class, userId);
    }
}
