package com.qoobot.qooerp.permission.util;

import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.permission.enums.DataScopeEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限SQL工具类
 * 用于构建数据权限过滤SQL
 */
@Slf4j
public class DataScopeSqlUtils {

    /**
     * 构建数据权限SQL
     *
     * @param originalSql 原始SQL
     * @param dataScope 数据权限范围
     * @return 添加了数据权限过滤的SQL
     */
    public static String buildDataScopeSql(String originalSql, DataScopeEnum dataScope) {
        if (dataScope == null || dataScope == DataScopeEnum.ALL) {
            // 全部数据，不需要过滤
            return originalSql;
        }

        try {
            // 解析SQL
            Statement statement = CCJSqlParserUtil.parse(originalSql);

            if (statement instanceof Select) {
                Select select = (Select) statement;
                PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

                // 构建数据权限条件
                Expression dataScopeExpression = buildDataScopeExpression(dataScope);

                // 添加到WHERE条件
                if (plainSelect.getWhere() == null) {
                    plainSelect.setWhere(dataScopeExpression);
                } else {
                    // 已有WHERE条件，使用AND连接
                    Parenthesis parenthesis = new Parenthesis(plainSelect.getWhere());
                    AndExpression andExpression = new AndExpression(parenthesis, dataScopeExpression);
                    plainSelect.setWhere(andExpression);
                }

                return select.toString();
            }
        } catch (JSQLParserException e) {
            log.error("解析SQL失败: {}", originalSql, e);
        }

        return originalSql;
    }

    /**
     * 构建数据权限表达式
     *
     * @param dataScope 数据权限范围
     * @return SQL表达式
     */
    private static Expression buildDataScopeExpression(DataScopeEnum dataScope) {
        Long userId = SecurityUtils.getUserId();

        switch (dataScope) {
            case ALL:
                // 全部数据，不需要过滤
                return null;

            case DEPT:
                // 本部门数据
                Long deptId = SecurityUtils.getDeptId();
                if (deptId != null) {
                    return new EqualsTo(new Column("dept_id"), new net.sf.jsqlparser.expression.StringValue(deptId.toString()));
                }
                break;

            case DEPT_AND_CHILD:
                // 本部门及子部门数据
                List<Long> deptIds = SecurityUtils.getDeptAndChildDeptIds();
                if (deptIds != null && !deptIds.isEmpty()) {
                    List<Expression> expressions = new ArrayList<>();
                    for (Long id : deptIds) {
                        expressions.add(new EqualsTo(new Column("dept_id"),
                                new net.sf.jsqlparser.expression.StringValue(id.toString())));
                    }
                    // 构建IN表达式
                    return new InExpression(
                            new Column("dept_id"),
                            new net.sf.jsqlparser.expression.operators.relational.ExpressionList(expressions)
                    );
                }
                break;

            case SELF:
                // 仅本人数据
                if (userId != null) {
                    return new EqualsTo(new Column("create_by"),
                            new net.sf.jsqlparser.expression.StringValue(userId.toString()));
                }
                break;

            case CUSTOM:
                // 自定义数据范围
                List<Long> customDeptIds = SecurityUtils.getCustomDeptIds();
                if (customDeptIds != null && !customDeptIds.isEmpty()) {
                    List<Expression> expressions = new ArrayList<>();
                    for (Long id : customDeptIds) {
                        expressions.add(new EqualsTo(new Column("dept_id"),
                                new net.sf.jsqlparser.expression.StringValue(id.toString())));
                    }
                    // 构建IN表达式
                    return new InExpression(
                            new Column("dept_id"),
                            new net.sf.jsqlparser.expression.operators.relational.ExpressionList(expressions)
                    );
                }
                break;

            default:
                break;
        }

        // 默认返回 false，不显示任何数据
        return new net.sf.jsqlparser.expression.operators.relational.EqualsTo(
                new net.sf.jsqlparser.expression.LongValue(1),
                new net.sf.jsqlparser.expression.LongValue(0)
        );
    }

    /**
     * 构建数据权限SQL（简化版本，不使用JSQLParser）
     * 适用于简单的SELECT语句
     *
     * @param originalSql 原始SQL
     * @param dataScope 数据权限范围
     * @param alias 表别名
     * @return 添加了数据权限过滤的SQL
     */
    public static String buildDataScopeSqlSimple(String originalSql, DataScopeEnum dataScope, String alias) {
        if (dataScope == null || dataScope == DataScopeEnum.ALL) {
            return originalSql;
        }

        Long userId = SecurityUtils.getUserId();
        String whereClause = "";

        String deptColumn = alias.isEmpty() ? "dept_id" : alias + ".dept_id";
        String userColumn = alias.isEmpty() ? "create_by" : alias + ".create_by";

        switch (dataScope) {
            case DEPT:
                Long deptId = SecurityUtils.getDeptId();
                if (deptId != null) {
                    whereClause = deptColumn + " = " + deptId;
                }
                break;

            case DEPT_AND_CHILD:
                List<Long> deptIds = SecurityUtils.getDeptAndChildDeptIds();
                if (deptIds != null && !deptIds.isEmpty()) {
                    whereClause = deptColumn + " IN (" + String.join(",", deptIds.stream()
                            .map(String::valueOf).collect(Collectors.toList())) + ")";
                }
                break;

            case SELF:
                if (userId != null) {
                    whereClause = userColumn + " = " + userId;
                }
                break;

            case CUSTOM:
                List<Long> customDeptIds = SecurityUtils.getCustomDeptIds();
                if (customDeptIds != null && !customDeptIds.isEmpty()) {
                    whereClause = deptColumn + " IN (" + String.join(",", customDeptIds.stream()
                            .map(String::valueOf).collect(Collectors.toList())) + ")";
                }
                break;

            default:
                break;
        }

        if (whereClause.isEmpty()) {
            // 如果没有构建出WHERE条件，返回原始SQL
            return originalSql;
        }

        // 添加WHERE条件
        String upperSql = originalSql.toUpperCase();
        int whereIndex = upperSql.indexOf(" WHERE ");

        if (whereIndex == -1) {
            // 没有WHERE，直接添加
            return originalSql + " WHERE " + whereClause;
        } else {
            // 已有WHERE，使用AND连接
            int orderByIndex = upperSql.indexOf(" ORDER BY ");
            int groupByIndex = upperSql.indexOf(" GROUP BY ");
            int limitIndex = upperSql.indexOf(" LIMIT ");

            int insertIndex = originalSql.length();

            if (orderByIndex != -1) {
                insertIndex = orderByIndex;
            } else if (groupByIndex != -1) {
                insertIndex = groupByIndex;
            } else if (limitIndex != -1) {
                insertIndex = limitIndex;
            }

            return originalSql.substring(0, insertIndex) + " AND (" + whereClause + ") " + originalSql.substring(insertIndex);
        }
    }
}
