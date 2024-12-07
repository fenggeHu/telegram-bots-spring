package tool.internal.cmd;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;
import tool.internal.bb.Interceptor;
import tool.utils.ExpressionParser;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拦截注解 @Cmd
 *
 * @author max.hu  @date 2024/11/05
 **/
@Slf4j
public class CmdInterceptor extends Interceptor {
    private Map<Method, CmdDTO> cachedCmdInfo = new ConcurrentHashMap<>();

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
            if (cmdDTO == null) {
                log.info("{}.{} return null", method.getDeclaringClass().getName(), method.getName());
                return null;
            }
        } else {
            cmdDTO = CmdDTO.builder().args(args).build();
            var cmdInfo = cachedCmdInfo.computeIfAbsent(method, m -> Cmd.Builder.of(m));
            String to;
            // 解析注解
            if (cmdInfo.getTo().contains("$")) { // 解析表达式
                to = ExpressionParser.str(cmdInfo.getTo(), buildParserParameters(method, args));
            } else {
                to = cmdInfo.getTo();
            }
            // 判断跳转 - to为空或者与本id相同时执行本方法
            if (null == to || to.isEmpty() || to.equals(cmdInfo.getId())) {
                return callable.call();
            }
            cmdDTO.setTo(to);
        }

        // 执行跳转
        String to = cmdDTO.to();
        if (!to.isEmpty()) {
            CmdMethod<?> next = CmdMethodKeeper.getMethod(ths, to);
            if (null == next) {
                throw new RuntimeException("CmdMethod - Command not found: " + to);
            }
            return next.invoke(cmdDTO.getArgs());
        } else {
            throw new RuntimeException(method.getDeclaringClass().getName() + "." + method.getName() + " - CmdMethod: " + cmdDTO);
        }
    }

}
