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
 * MVEL：简单易用、性能较好，比较适合规则简单的模板解析场景
 * 关于MVEL传参的用法：
 * TemplateRuntime.execute 和 MVEL.eval的上下文参数都可以传入2个：Object ctx和Map<String, Object> vars
 * ctx:  传入对象，在表达式里使用对象的属性。如ctx = new User(id=10)，则表达式“id==10”返回True
 * vars: 传入Map，在表达式里使用key.对象的属性。
 * CmdInfo ci1 = CmdInfo.builder().id("hello").to("world").notes("teeee").build();
 * CmdInfo ci2 = CmdInfo.builder().id("id2").to("world").notes("I'm 20").build();
 * vars.put("ci1", ci1);
 * vars.put("ci2", ci2);
 * var result = MVEL.eval("ci1.to == ci2.to", vars); // return True
 *
 * @author max.hu  @date 2024/11/6
 * @description 解析表达式
 **/
public class ExpressionParser {
    private static final Map<String, CompiledTemplate> cached = new ConcurrentHashMap<>();

    // 使用ParserContext引入常用类
    private static ParserContext parserContext = new ParserContext();

    static {
//        parserContext.setStrictTypeEnforcement(true);
        parserContext.addImport(Arrays.class);
        parserContext.addImport(Collections.class);
        parserContext.addImport("PV", PrimitiveValueUtil.class);
    }

    /**
     * 用x0...n当作Map变量key
     */
    private static Map<String, Object> xVars(Object... args) {
        Map<String, Object> vars = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            vars.put("x" + i, args[i]);
        }
        return vars;
    }

    private static String keyPrefix(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : args) {
            sb.append(obj.getClass().getName()).append("_");
        }

        return sb.toString();
    }

    //根据传入的args长度处理
    // FIXME 对相同的一个表达式字符串“expression”，传入不同的参数args有些情况下报错。
    //  现象：TemplateRuntime.execute(template, vars)执行时匹配第一次使用的args类型，好像template缓存了参数类型
    public static Object execute(String expression, Object... args) {
        // cached key带args，避免传入不同参数出现报错的情况
        String prefix = keyPrefix(args);
        CompiledTemplate template = cached.computeIfAbsent(prefix + expression,
                k -> TemplateCompiler.compileTemplate(expression, parserContext));
        try {
            // 处理参数规则
            if (args.length == 1) {
                return TemplateRuntime.execute(template, args[0]);
                // 只有一个参数的时候可以同时使用对象的属性和Map的Xn.属性
//                if (args[0] instanceof Map) {   // 单独处理入参为Map的情况
//                    return TemplateRuntime.execute(template, args[0]);
//                } else {
//                    Map<String, Object> vars = xVars(args); // 用x0...n当作Map变量key
//                    return TemplateRuntime.execute(template, args[0], vars);
//                }
            } else {
                Map<String, Object> vars = xVars(args); // 多个入参时用x0...n当作Map变量key
                return TemplateRuntime.execute(template, vars);
            }
        } catch (Exception e) {
            throw new RuntimeException("Please check expression: " + expression, e);
        }
    }


    @SneakyThrows
    public static String str(String expression, Object... args) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }
        var result = execute(expression, args);
        return PrimitiveValueUtil.stringValue(result);
    }

    /**
     * 计算表达式
     *
     * @param expression
     * @param args
     * @return
     */
    public static Object eval(String expression, Object... args) {
        if (null == expression || expression.isEmpty()) {
            return expression;
        }
        try {
            // 处理参数规则
            if (args.length == 1) { // 只有一个参数的时候可以同时使用对象的属性和Map的Xn.属性
                if (args[0] instanceof Map) {   // 单独处理入参为Map的情况
                    return MVEL.eval(expression, args[0]);
                } else {
                    Map<String, Object> vars = xVars(args); // 用x0...n当作Map变量key
                    return MVEL.eval(expression, args[0], vars);    // 只有一个参数时，可以使用对象的属性key或者x0.key
                }
            } else {
                Map<String, Object> vars = xVars(args); // 用x0...n当作Map变量key
                return MVEL.eval(expression, vars);
            }
        } catch (Exception e) {
            throw new RuntimeException("Please check expression and args: " + expression, e);
        }
    }

    public static boolean bool(String expression, Object... args) {
        if (null == expression || expression.isEmpty()) {
            return Boolean.FALSE;
        }

        var result = eval(expression, args);
        return PrimitiveValueUtil.boolValue(result);
    }

}
