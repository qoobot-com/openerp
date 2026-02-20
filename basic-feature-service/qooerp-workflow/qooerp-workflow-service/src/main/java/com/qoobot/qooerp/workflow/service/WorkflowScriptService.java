package com.qoobot.qooerp.workflow.service;

import java.util.Map;

/**
 * 流程脚本服务
 * 提供流程脚本的执行、验证、管理等功能
 */
public interface WorkflowScriptService {

    /**
     * 执行脚本
     * @param scriptType 脚本类型（javascript, groovy等）
     * @param scriptContent 脚本内容
     * @param variables 变量
     * @return 执行结果
     */
    Object executeScript(String scriptType, String scriptContent, Map<String, Object> variables);

    /**
     * 验证脚本语法
     * @param scriptType 脚本类型
     * @param scriptContent 脚本内容
     * @return 验证结果
     */
    Map<String, Object> validateScript(String scriptType, String scriptContent);

    /**
     * 格式化脚本
     * @param scriptType 脚本类型
     * @param scriptContent 脚本内容
     * @return 格式化后的脚本
     */
    String formatScript(String scriptType, String scriptContent);

    /**
     * 获取脚本模板
     * @param templateName 模板名称
     * @return 模板内容
     */
    String getScriptTemplate(String templateName);

    /**
     * 测试脚本执行
     * @param scriptType 脚本类型
     * @param scriptContent 脚本内容
     * @param variables 测试变量
     * @return 执行结果和输出
     */
    Map<String, Object> testScript(String scriptType, String scriptContent, Map<String, Object> variables);
}
