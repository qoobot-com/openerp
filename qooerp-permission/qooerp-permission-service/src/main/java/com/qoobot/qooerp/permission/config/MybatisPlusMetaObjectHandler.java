package com.qoobot.qooerp.permission.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.qoobot.qooerp.common.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 元数据自动填充处理器
 */
@Slf4j
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 创建人
        try {
            String username = SecurityUtils.getUsername();
            this.strictInsertFill(metaObject, "createBy", String.class, username);
            this.strictInsertFill(metaObject, "updateBy", String.class, username);
        } catch (Exception e) {
            // 如果获取不到用户信息，使用默认值
            this.strictInsertFill(metaObject, "createBy", String.class, "system");
            this.strictInsertFill(metaObject, "updateBy", String.class, "system");
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 更新人
        try {
            String username = SecurityUtils.getUsername();
            this.strictUpdateFill(metaObject, "updateBy", String.class, username);
        } catch (Exception e) {
            // 如果获取不到用户信息，使用默认值
            this.strictUpdateFill(metaObject, "updateBy", String.class, "system");
        }
    }
}
