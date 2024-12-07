package tool.internal.bb;

import lombok.Setter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 为拦截器注入/保留必要的数据
 *
 * @author max.hu  @date 2024/11/11
 **/
public class Interceptor {
    @Setter
    protected InterceptConfig config;

    // 为表达式解析器构建参数
    protected Object buildParserParameters(Method m, Object[] args) {
        Map<String, Object> ret = new HashMap<>();
        if (null == m || null == args || args.length == 0) return ret;
        if (args.length == 1) {
            return args[0];
        }
        var params = m.getParameters();
        if (params.length != args.length) {
            throw new RuntimeException("Something got Wrong: " + m.getName());
        }
        for (int i = 0; i < params.length; i++) {
            ret.put(params[i].getName(), args[i]);
            // 按位置传递参数值
            ret.put("arg" + i, args[i]);
        }
        return ret;
    }
}
