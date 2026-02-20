package com.qoobot.qooerp.notify.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板解析器
 */
@Slf4j
@Component
public class TemplateParser {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * 解析模板，替换变量
     *
     * @param template 模板内容
     * @param variables 变量映射
     * @return 解析后的内容
     */
    public String parse(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        if (variables == null || variables.isEmpty()) {
            return template;
        }

        try {
            Matcher matcher = VARIABLE_PATTERN.matcher(template);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                String variableName = matcher.group(1);
                Object value = variables.get(variableName);

                String replacement = value != null ? value.toString() : "";
                matcher.appendReplacement(result, replacement);
            }

            matcher.appendTail(result);
            return result.toString();
        } catch (Exception e) {
            log.error("模板解析失败: template={}", template, e);
            return template;
        }
    }

    /**
     * 检查模板中是否包含未定义的变量
     *
     * @param template 模板内容
     * @param variables 变量映射
     * @return 是否包含未定义的变量
     */
    public boolean hasUndefinedVariables(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return false;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            if (!variables.containsKey(variableName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 提取模板中的所有变量名
     *
     * @param template 模板内容
     * @return 变量名列表
     */
    public java.util.Set<String> extractVariables(String template) {
        java.util.Set<String> variables = new java.util.HashSet<>();

        if (template == null || template.isEmpty()) {
            return variables;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }

        return variables;
    }
}
