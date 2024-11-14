package tool.internal.cmd;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;
import tool.internal.bb.Interceptor;
import tool.utils.ExpressionParser;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拦截注解 @Cmd
 *
 * @author max.hu  @date 2024/11/05
 **/
@Slf4j
public class CmdInterceptor extends Interceptor {
    @SneakyThrows
    @RuntimeType
//    @Advice.OnMethodEnter
    public Object intercept(@This Object ths,   // 代理类实例 -- @Super Object obj.target == ths
                            @SuperMethod Method superMethod,    // 代理类当前的方法
                            @AllArguments Object[] args, // 方法的入参
                            @Origin Method method,  // 被代理的原方法
                            @SuperCall Callable<?> callable) {

        CmdDTO cmdDTO;
        // 优先级1，如果method returnType是CmdDTO class或继承自ToCmd class
        if (method.getReturnType().isAssignableFrom(CmdDTO.class)) {
            cmdDTO = (CmdDTO) callable.call();
        } else {
            var cmdInfo = Cmd.Builder.of(method);
            String to = ExpressionParser.execute(cmdInfo.getId(), cmdInfo.getTo(), buildParameters(method, args));
            // 判断跳转 - to为空或者与本id相同时执行本方法
            if (to.isEmpty() || to.equals(cmdInfo.getId())) {
                return callable.call();
            }
            cmdDTO = CmdDTO.builder().to(to).args(args).build();
        }

        // 执行跳转
        String to = cmdDTO.to();
        if (!to.isEmpty()) {
            CmdMethod<?> next = toCmdMethod(cmdDTO.to(), ths);
            return Objects.requireNonNull(next).invoke(cmdDTO.getArgs());
        } else {
            throw new RuntimeException(method.getDeclaringClass().getName() + "." + method.getName() +
                    " - CmdMethod: " + cmdDTO);
        }
    }

    // 缓存
    private Map<String, CmdMethod> cachedMethods = new ConcurrentHashMap<>();

    // 查找
    private CmdMethod toCmdMethod(String to, Object obj) {
        var cm = cachedMethods.get(to);
        if (cm != null) {
            return cm;
        }
        cm = CmdMethodKeeper.getMethod(obj.getClass().getName(), to);
        if (null == cm) {
            throw new RuntimeException("CmdMethod not found: " + to);
        }
        this.cachedMethods.put(to, cm);
        return cm;
    }

}
