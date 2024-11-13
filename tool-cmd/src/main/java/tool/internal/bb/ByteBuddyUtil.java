package tool.internal.bb;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import tool.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 拦截器注入 eg:
 * Builder.method(ElementMatchers.named("cmd1"))
 * .intercept(MethodDelegation.to(Interceptor1.class))
 * .method(ElementMatchers.anyOf(Method... ).or(ElementMatchers.named("cmd3")))
 * .intercept(MethodDelegation.to(Interceptor2.class))
 * .make()
 * .load(Main.class.getClassLoader())
 * .getLoaded()
 * .getDeclaredConstructor()
 * .newInstance();
 *
 * @author max.hu  @date 2024/11/05
 **/
@Slf4j
public class ByteBuddyUtil {
    /**
     * 按注解对应拦截器
     */
    public static Class<?> buildClass(Class<?> clazz, Map<Class<? extends Annotation>, Class<? extends Interceptor>> aic) {
        InterceptConfig config = InterceptConfig.builder().id(clazz.getSimpleName()).clazz(clazz).build();
        for (Map.Entry<Class<? extends Annotation>, Class<? extends Interceptor>> entry : aic.entrySet()) {
            var an = entry.getKey();
            List<Method> methods = ClassUtil.getDeclaredMethodsWithAnnotation(clazz, an, false);
            config.addMethod(entry.getValue(), methods.toArray(new Method[0]));
        }

        return buildClass(config);
    }

    /**
     * 构建class
     * 在使用 ByteBuddy 时，如果你想要对同一个 Method 应用多个拦截器（Interceptor），你可以通过 链式拦截器（Chain of Responsibility）模式来实现，或者简单地通过多个拦截器顺序地组合。
     * ByteBuddy 不直接提供将多个拦截器同时应用于同一个方法的机制，但你可以利用 MethodDelegation 和 Advice 两种方式来顺序地链式执行多个拦截器。这样，多个拦截器可以按顺序作用于同一个方法。
     */
    public static Class<?> buildClass(final InterceptConfig config) {
        if (null == config || config.getClazz() == null) {
            throw new RuntimeException("ByteBuddyConfig Err: " + config);
        }

        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition typeDefinition = null;
        for (Map.Entry<Method, Class<? extends Interceptor>[]> entry : config.getMethods().entrySet()) {
            var m = entry.getKey();
            try {
                Implementation.Composable md = null;
                for (Class<? extends Interceptor> icc : entry.getValue()) {
                    var ic = (Interceptor) icc.getDeclaredConstructor().newInstance();
                    ic.setConfig(config);
                    if (null == md) {
                        md = MethodDelegation.to(ic);
                    } else {
                        md = md.andThen(MethodDelegation.to(ic));
                    }
                }

                if (null == typeDefinition) {
                    typeDefinition = getByteBuddy().subclass(config.getClazz()).method(ElementMatchers.anyOf(m)).intercept(md);
                } else {
                    typeDefinition = typeDefinition.method(ElementMatchers.anyOf(m)).intercept(md);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        var ret = typeDefinition.make().load(config.getClazz().getClassLoader()).getLoaded();
        config.m2m(ret);
        return ret;
    }

    // 自定义命名
    private static ByteBuddy getByteBuddy() {
        return new ByteBuddy(ClassFileVersion.JAVA_V11); //.with(new NamingStrategy.Suffixing(id));
    }

//    // 可以自定义命名
//    static class MyNamingStrategy extends NamingStrategy.AbstractBase {
//        String suffix;
//
//        public MyNamingStrategy(String suffix) {
//            this.suffix = suffix;
//        }
//
//        @Override
//        protected String name(TypeDescription superClass) {
//            if (null == suffix) {
//                return superClass.getName();
//            } else {
//                return superClass.getName() + ClassNameSign + suffix;
//            }
//        }
//    }
}
