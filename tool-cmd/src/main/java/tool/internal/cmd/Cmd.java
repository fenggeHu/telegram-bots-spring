package tool.internal.cmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * 命令
 * 通过表达式路由命令 - expression --> Cmd
 *
 * @author max.hu  @date 2024/11/01
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cmd {
    /**
     * 唯一标识ID - 同也是目标标识
     */
    String value() default "";

    /**
     * to下一个目标：跳转的目标Method的入参、出参必须与当前方法一致
     * 可以通过表达式引擎计算目标
     */
    String to() default "";

    /**
     * 注释
     */
    String notes() default "";

    /**
     * 注解转Cmd information
     */
    class Builder {
        // 计算id
        public static String id(Method method) {
            Cmd clazz = method.getDeclaringClass().getAnnotation(Cmd.class);
            Cmd cmd = method.getAnnotation(Cmd.class);
            String root = null == clazz ? "" : clazz.value()
                    .replace("//", "/").replace("//", "/").trim();
            var ns = cmd.value().replace("//", "/").replace("//", "/").trim();
            String path = !ns.isEmpty() ? ns : method.getName();
            var id = ("/" + root + (path.startsWith("/") ? path : "/" + path)).replace("//", "/");
            return id;
        }

        public static CmdDTO of(Method method) {
            // 查询class是否配置了路径
            Cmd clazz = method.getDeclaringClass().getAnnotation(Cmd.class);
            Cmd cmd = method.getAnnotation(Cmd.class);
            String root = null == clazz ? "" : clazz.value()
                    .replace("//", "/").replace("//", "/").trim();
            var ns = cmd.value().replace("//", "/").replace("//", "/").trim();
            String path = !ns.isEmpty() ? ns : method.getName();
            var id = ("/" + root + (path.startsWith("/") ? path : "/" + path)).replace("//", "/");
            return CmdDTO.builder().id(id).to(cmd.to().trim()).build();
        }

    }
}
