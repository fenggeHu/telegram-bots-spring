package tool.internal.cmd;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author max.hu  @date 2024/11/6
 * @description 解析表达式
 **/
public class ExpressionParser {
    private static final Map<String, CompiledTemplate> cache = new ConcurrentHashMap<>();

    public static String execute(String key, String expression, Map<String, Object> vars) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }

        CompiledTemplate template = cache.get(key);
        if (template == null) {
            template = TemplateCompiler.compileTemplate(expression);
            cache.put(key, template);
        }

        Object result = TemplateRuntime.execute(template, vars);
        return null == result ? null : String.valueOf(result);
    }

}
