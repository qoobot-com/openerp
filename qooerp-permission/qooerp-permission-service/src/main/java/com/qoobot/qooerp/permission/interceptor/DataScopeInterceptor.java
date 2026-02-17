package com.qoobot.qooerp.permission.interceptor;

import com.qoobot.qooerp.permission.aspect.DataScopeContextHolder;
import com.qoobot.qooerp.permission.enums.DataScopeEnum;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import com.qoobot.qooerp.permission.util.DataScopeSqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 数据权限拦截器
 * 在SQL执行前注入数据权限过滤条件
 */
@Slf4j
@Component
@Intercepts({
    @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
    ),
    @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
    )
})
@RequiredArgsConstructor
public class DataScopeInterceptor implements Interceptor {

    private final PermissionRoleService roleService;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 检查是否需要数据权限过滤
        if (!DataScopeContextHolder.needFilter()) {
            return invocation.proceed();
        }

        // 获取数据权限范围
        DataScopeEnum dataScope = DataScopeContextHolder.get();
        log.debug("数据权限拦截器工作，当前权限范围: {}", dataScope.getDesc());

        // 获取MappedStatement
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        // 获取原始SQL
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String originalSql = boundSql.getSql();

        // 构建数据权限SQL
        String dataScopeSql = DataScopeSqlUtils.buildDataScopeSql(originalSql, dataScope);

        if (!originalSql.equals(dataScopeSql)) {
            log.debug("原始SQL: {}", originalSql);
            log.debug("数据权限SQL: {}", dataScopeSql);

            // 使用反射修改BoundSql中的SQL
            org.apache.ibatis.reflection.MetaObject metaObject = org.apache.ibatis.reflection.SystemMetaObject.forObject(boundSql);
            metaObject.setValue("sql", dataScopeSql);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以在这里配置一些参数
    }
}
