package tool.utils;

import lombok.SneakyThrows;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author max.hu  @date 2024/11/6
 * @description 解析表达式
 **/
public class ExpressionParser {
    private static final Map<String, CompiledTemplate> cached = new ConcurrentHashMap<>();

    // 把expression当作cache key
    public static String str(String expression, Map<String, Object> vars) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }
        return str(expression, expression, vars);
    }

    // 使用ParserContext引入常用类
    private static ParserContext parserContext = new ParserContext();

    static {
//        parserContext.setStrictTypeEnforcement(true);
        parserContext.addImport(Arrays.class);
        parserContext.addImport(Collections.class);
    }

    public static Object execute(String key, String expression, Map<String, Object> vars) {
        CompiledTemplate template = cached.computeIfAbsent(key,
                k -> TemplateCompiler.compileTemplate(expression, parserContext));
        try {
            return TemplateRuntime.execute(template, vars);
        } catch (Exception e) {
            throw new RuntimeException(key + " - Please check expression: " + expression, e);
        }
    }

    // 转成字符串
    public static String str(String key, String expression, Map<String, Object> vars) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }
        var result = execute(key, expression, vars);
        return null == result ? null : String.valueOf(result);
    }

    // 其它的变形
    public static boolean boolX(String expression, Object... args) {
        if (null == expression || expression.isEmpty()) {
            return Boolean.FALSE;
        }

        Map<String, Object> vars = xVars(args);
        var expr = str(expression, vars);   // TODO 能不能一步到位？
        var result = MVEL.eval(expr);
        return PrimitiveValueUtil.boolValue(result);
    }

    private static Map<String, Object> xVars(Object... args) {
        Map<String, Object> vars = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            vars.put("x" + i, args[i]);
        }
        return vars;
    }

    /**
     * 用x0...n表示变量key
     */
    @SneakyThrows
    public static String strX(String expression, Object... args) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }

        Map<String, Object> vars = xVars(args);

        return str(expression, vars);
    }
}
