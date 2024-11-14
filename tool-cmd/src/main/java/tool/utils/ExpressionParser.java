package tool.utils;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author max.hu  @date 2024/11/6
 * @description 解析表达式
 **/
public class ExpressionParser {
    private static final Map<String, CompiledTemplate> cache = new ConcurrentHashMap<>();

    // 把expression当作cache key
    public static String execute(String expression, Map<String, Object> vars) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }
        return execute(expression, expression, vars);
    }

    public static String execute(String key, String expression, Map<String, Object> vars) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }

        CompiledTemplate template = cache.get(key);
        if (template == null) {
            template = TemplateCompiler.compileTemplate(expression);
            cache.put(key, template);
        }
        try {
            Object result = TemplateRuntime.execute(template, vars);
            return null == result ? null : String.valueOf(result);
        } catch (Exception e) {
            throw new RuntimeException(key + " - Please check expression: " + expression);
        }
    }

    // 其它的变形
    public static String execute(String expression, Object... args) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }
        Map<String, Object> vars = new HashMap<>(args.length);
        for (int i = 0; i < args.length; i++) {
            vars.put("arg" + i, args[i]);
        }
        return execute(expression, vars);
    }
}
