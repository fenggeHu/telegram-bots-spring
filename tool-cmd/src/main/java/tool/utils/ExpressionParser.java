package tool.utils;

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

    // 把expression当作cache key
    public static String str(String expression, Map<String, Object> vars) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }
        return str(expression, expression, vars);
    }

    public static Object execute(String key, String expression, Map<String, Object> vars) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }

        CompiledTemplate template = cache.get(key);
        if (template == null) {
            template = TemplateCompiler.compileTemplate(expression);
            cache.put(key, template);
        }
        try {
            return TemplateRuntime.execute(template, vars);
        } catch (Exception e) {
            throw new RuntimeException(key + " - Please check expression: " + expression, e);
        }
    }

    // 转成字符串
    public static String str(String key, String expression, Map<String, Object> vars) {
        var result = execute(key, expression, vars);
        return null == result ? null : String.valueOf(result);
    }

    // 其它的变形

    /**
     * 用x0...n表示变量key
     */
//    @SneakyThrows
//    public static String strX(String expression, Object... args) {
//        if (null == expression || expression.isEmpty()) {
//            return expression;
//        }
//        ExpressRunner runner = new ExpressRunner();
//        DefaultContext<String, Object> vars = new DefaultContext<>();
//        for (int i = 0; i < args.length; i++) {
//            vars.put("x" + i, args[i]);
//        }
//
//        Object obj = runner.execute(expression, vars, null, true, false);
//        return null == obj ? null : String.valueOf(obj);
//    }
//
//    public static void main(String[] args) {
//        var arr = new String[]{"a1", "a2", "a3"};
//        var list = new ArrayList<>(List.of("e1", "e2"));
//        var map = Map.of("k1", "v1", "k2", "v2");
//
//        var s = strX("排序:\n Arrays.toString(x0) + x1.get(0)", arr, list, map);
//        System.out.println(s);
//    }
}
