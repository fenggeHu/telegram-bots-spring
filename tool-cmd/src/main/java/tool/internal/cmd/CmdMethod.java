package tool.internal.cmd;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import tool.utils.ClassUtil;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 在Method上的Cmd注解信息
 * 1，当表达式为expression = True时 -> method
 *
 * @author max.hu  @date 2024/11/04
 **/
@Data
@Builder
public class CmdMethod<T> {
    // 唯一ID - cmd注解的value，默认取方法名
    protected String value;
    // origin class
    protected Class<T> clazz;
    // cmd class
    protected Class<T> cmdClass;
    // object
    protected T cmdObject;
    // 方法
    protected Method method;
    // 代理后的方法
    protected Method cmdMethod;

    /**
     * 为了一些方法跳转的时候兼容性更好做一些入参的适配
     */
    @SneakyThrows
    public Object invoke(Object... args) {
        Object[] params = matchArgs(this.cmdMethod.getParameterTypes(), args);
        return this.cmdMethod.invoke(cmdObject, params);
    }

    // 参数适配： FIXME：参数的匹配逻辑 - 待优化
    private Object[] matchArgs(final Class<?>[] paramTypes, final Object... args) {
        if (paramTypes.length == 0) return null;

        Object[] ret = new Object[paramTypes.length];
        List<Object> objects = new LinkedList<>();
        for (var obj : args) {
            objects.add(obj);
        }
        for (int i = 0; i < paramTypes.length; i++) {
            for (Object obj : objects) {
                // 判断对象是否能赋值给参数
                if (obj.getClass().isAssignableFrom(paramTypes[i])) {
                    ret[i] = obj;
                    objects.remove(obj);
                    break;
                }
            }
        }

        return ret;
    }

    // 补充cmd method属性
    public static void fillCmdMethod(final Map<String, CmdMethod> map, Class<?> cmdClass) {
        fillCmdMethod(map.values(), cmdClass);
    }

    public static void fillCmdMethod(final Collection<CmdMethod> coll, Class<?> cmdClass) {
        Method[] cmdMethods = cmdClass.getDeclaredMethods();
        for (CmdMethod cm : coll) {
            cm.setCmdMethod(getCmdClassMethod(cmdMethods, cm.getMethod()));
            cm.setCmdClass(cmdClass);
        }
    }


    // 提取cmd method
    public static <T> Map<String, CmdMethod> getCmdMethod(Class<T> clazz) {
        Map<String, CmdMethod> ret = new HashMap<>();
        var methods = ClassUtil.getDeclaredMethodsWithAnnotation(clazz, Cmd.class, false);
        if (methods.isEmpty()) {
            return ret;
        }
        for (Method m : methods) {
            CmdInfo ci = Cmd.Builder.of(m);
            CmdMethod cmi = CmdMethod.<T>builder()
                    .value(ci.getId())
                    .clazz(clazz)
                    .method(m)
                    .build();
            ret.put(cmi.getValue(), cmi);
        }
        return ret;
    }

    // 通过原方法找到bytebuddy转换后的方法
    private static Method getCmdClassMethod(Method[] cmdMethods, Method origin) {
        for (Method n : cmdMethods) {
            if (n.getName().equals(origin.getName()) && n.getReturnType() == origin.getReturnType()
                    && ClassUtil.isMethodSignatureSame(n, origin)) {
                return n;
            }
        }
        return null;
    }
}
