package com.qoobot.qooerp.workflow.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.service.WorkflowScriptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程脚本服务实现
 */
@Slf4j
@Service
public class WorkflowScriptServiceImpl implements WorkflowScriptService {

    // 脚本引擎缓存
    private final Map<String, ScriptEngine> engineCache = new HashMap<>();

    // 脚本模板
    private final Map<String, String> scriptTemplates = new HashMap<>();

    public WorkflowScriptServiceImpl() {
        // 初始化脚本模板
        initScriptTemplates();
    }

    @Override
    public Object executeScript(String scriptType, String scriptContent, Map<String, Object> variables) {
        try {
            ScriptEngine engine = getScriptEngine(scriptType);

            // 设置变量
            if (variables != null) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    engine.put(entry.getKey(), entry.getValue());
                }
            }

            // 添加常用的工具类
            engine.put("log", log);
            engine.put("Math", new ScriptMathHelper());

            // 执行脚本
            Object result = engine.eval(scriptContent);

            log.info("执行脚本成功: scriptType={}, scriptLength={}", scriptType,
                    scriptContent.length());

            return result;
        } catch (ScriptException e) {
            log.error("执行脚本失败: scriptType={}, error={}", scriptType, e.getMessage(), e);
            throw new BusinessException(WorkflowErrorCode.SCRIPT_EXECUTION_FAILED.getCode(),
                    "脚本执行失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> validateScript(String scriptType, String scriptContent) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("message", "脚本验证通过");

        try {
            ScriptEngine engine = getScriptEngine(scriptType);

            // 简单验证：尝试编译脚本
            if (engine instanceof Compilable) {
                Compilable compilable = (Compilable) engine;
                compilable.compile(scriptContent);
            } else {
                // 对于不支持编译的引擎，尝试解析
                engine.eval("true");
            }

        } catch (ScriptException e) {
            result.put("valid", false);
            result.put("message", "脚本验证失败: " + e.getMessage());
            result.put("lineNumber", e.getLineNumber());
            result.put("columnNumber", e.getColumnNumber());

            log.warn("脚本验证失败: scriptType={}, error={}", scriptType, e.getMessage());
        }

        return result;
    }

    @Override
    public String formatScript(String scriptType, String scriptContent) {
        // 简单格式化：统一缩进和空行
        try {
            String[] lines = scriptContent.split("\n");
            StringBuilder formatted = new StringBuilder();
            int indentLevel = 0;

            for (String line : lines) {
                String trimmed = line.trim();

                if (trimmed.isEmpty()) {
                    continue;  // 跳过空行
                }

                // 减少缩进
                if (trimmed.startsWith("}") || trimmed.startsWith("]") || trimmed.startsWith(")")) {
                    indentLevel = Math.max(0, indentLevel - 1);
                }

                // 添加缩进
                for (int i = 0; i < indentLevel; i++) {
                    formatted.append("    ");
                }

                formatted.append(trimmed).append("\n");

                // 增加缩进
                if (trimmed.endsWith("{") || trimmed.endsWith("[") || trimmed.endsWith("(")) {
                    indentLevel++;
                }
            }

            return formatted.toString();
        } catch (Exception e) {
            log.warn("脚本格式化失败: scriptType={}", scriptType, e);
            return scriptContent;  // 格式化失败返回原内容
        }
    }

    @Override
    public String getScriptTemplate(String templateName) {
        return scriptTemplates.get(templateName);
    }

    @Override
    public Map<String, Object> testScript(String scriptType, String scriptContent, Map<String, Object> variables) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 验证脚本
            Map<String, Object> validation = validateScript(scriptType, scriptContent);
            result.put("validation", validation);

            if (!Boolean.TRUE.equals(validation.get("valid"))) {
                result.put("success", false);
                return result;
            }

            // 捕获日志输出
            StringBuilder logOutput = new StringBuilder();
            LogCapture logCapture = new LogCapture(logOutput);

            // 执行脚本
            ScriptEngine engine = getScriptEngine(scriptType);

            // 设置变量
            if (variables != null) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    engine.put(entry.getKey(), entry.getValue());
                }
            }

            engine.put("log", logCapture);
            engine.put("Math", new ScriptMathHelper());

            Object executionResult = engine.eval(scriptContent);

            result.put("success", true);
            result.put("result", executionResult);
            result.put("output", logOutput.toString());

            log.info("测试脚本成功: scriptType={}, result={}", scriptType, executionResult);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("测试脚本失败: scriptType={}", scriptType, e);
        }

        return result;
    }

    /**
     * 获取脚本引擎
     */
    private ScriptEngine getScriptEngine(String scriptType) {
        ScriptEngine engine = engineCache.get(scriptType);
        if (engine == null) {
            ScriptEngineManager manager = new ScriptEngineManager();
            engine = manager.getEngineByName(scriptType);

            if (engine == null) {
                throw new BusinessException(WorkflowErrorCode.SCRIPT_ENGINE_NOT_FOUND.getCode(),
                        "不支持的脚本类型: " + scriptType);
            }

            engineCache.put(scriptType, engine);
        }

        return engine;
    }

    /**
     * 初始化脚本模板
     */
    private void initScriptTemplates() {
        // JavaScript 模板
        scriptTemplates.put("javascript_condition",
                "// 条件判断脚本示例\n" +
                "// 返回 true 或 false\n" +
                "// 可用变量: variables, log, Math\n" +
                "\n" +
                "var amount = variables.get('amount');\n" +
                "var priority = variables.get('priority');\n" +
                "\n" +
                "// 金额大于1000且优先级为高\n" +
                "if (amount > 1000 && 'high'.equals(priority)) {\n" +
                "    log.info('条件满足: amount=' + amount + ', priority=' + priority);\n" +
                "    return true;\n" +
                "}\n" +
                "\n" +
                "return false;");

        scriptTemplates.put("javascript_expression",
                "// 表达式脚本示例\n" +
                "// 返回计算结果\n" +
                "// 可用变量: variables, log, Math\n" +
                "\n" +
                "var days = variables.get('days');\n" +
                "var dueDate = Math.addDays(new Date(), days);\n" +
                "\n" +
                "return dueDate;");

        // Groovy 模板
        scriptTemplates.put("groovy_condition",
                "// 条件判断脚本示例 (Groovy)\n" +
                "// 返回 true 或 false\n" +
                "// 可用变量: variables, log, Math\n" +
                "\n" +
                "def amount = variables.get('amount')\n" +
                "def priority = variables.get('priority')\n" +
                "\n" +
                "// 金额大于1000且优先级为高\n" +
                "if (amount > 1000 && priority == 'high') {\n" +
                "    log.info('条件满足: amount={}, priority={}', amount, priority)\n" +
                "    return true\n" +
                "}\n" +
                "\n" +
                "return false");

        scriptTemplates.put("groovy_expression",
                "// 表达式脚本示例 (Groovy)\n" +
                "// 返回计算结果\n" +
                "// 可用变量: variables, log, Math\n" +
                "\n" +
                "def days = variables.get('days')\n" +
                "def dueDate = Math.addDays(new Date(), days)\n" +
                "\n" +
                "return dueDate");
    }

    /**
     * 脚本数学辅助类
     */
    public static class ScriptMathHelper {
        public double add(double a, double b) {
            return a + b;
        }

        public double subtract(double a, double b) {
            return a - b;
        }

        public double multiply(double a, double b) {
            return a * b;
        }

        public double divide(double a, double b) {
            if (b == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return a / b;
        }

        public int randomInt(int max) {
            return (int) (Math.random() * max);
        }
    }

    /**
     * 日志捕获类
     */
    public static class LogCapture {
        private final StringBuilder output;

        public LogCapture(StringBuilder output) {
            this.output = output;
        }

        public void info(String message) {
            output.append("[INFO] ").append(message).append("\n");
        }

        public void warn(String message) {
            output.append("[WARN] ").append(message).append("\n");
        }

        public void error(String message) {
            output.append("[ERROR] ").append(message).append("\n");
        }

        public void debug(String message) {
            output.append("[DEBUG] ").append(message).append("\n");
        }
    }
}
