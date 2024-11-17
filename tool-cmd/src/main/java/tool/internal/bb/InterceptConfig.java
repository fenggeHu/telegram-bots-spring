package tool.internal.bb;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import tool.utils.ClassUtil;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 通过bytebuddy给类注入拦截器
 * 此处只实现一个Method使用一个拦截器。bytebuddy可以使用
 * @author max.hu  @date 2024/11/05
 **/
@Slf4j
@Data
@Builder
public class InterceptConfig {
    // 唯一ID
    private String id;
    // 原类
    private Class<?> clazz;
    // 原类的方法对应拦截器
    private Map<Method, Class<? extends Interceptor>[]> methods;

    // 拦截器 + 方法
    public InterceptConfig addMethod(Class<? extends Interceptor> intercept, Method... ms) {
        if (null == intercept || null == ms) {
            return this;
        }
        if (null == this.methods) {
            this.methods = new HashMap<>();
        }
        for (Method m : ms) {
            var ics = this.methods.get(m);
            if (null == ics) {
                ics = new Class[]{intercept};
                this.methods.put(m, ics);
            } else {
                var set = new HashSet<>(Arrays.asList(ics));
                set.add(intercept);
                this.methods.put(m, set.toArray(new Class[0]));
            }
        }

        return this;
    }

    // 其它配置
    // DeclaredConstructor: Class<?>... parameterTypes

    /**
     * BB代理后的类和方法关系
     */
    // BB类
    private Class<?> bbClazz;
    /**
     * 拦截前后的方法对应.
     * * 原方法 -> bb处理后的方法
     */
    private Map<Method, Method> m2m;

    public InterceptConfig m2m(Class<?> bbClazz) {
        this.bbClazz = bbClazz;
        return m2m();
    }

    public InterceptConfig m2m() {
        if (null == clazz || null == bbClazz || methods == null) {
            log.warn("config is error");
            return this;
        }
        if (null == m2m) {
            m2m = new HashMap<>();
        }
        var origins = new HashSet<Method>();
        this.methods.keySet().forEach(vs -> origins.addAll(List.of(vs)));
        var bbMethods = bbClazz.getDeclaredMethods();
        for (Method ori : origins) {
            for (Method bb : bbMethods) {
                if (ClassUtil.isMethodSignatureSame(ori, bb)) {
                    m2m.put(ori, bb);
                    break;
                }
            }
        }

        return this;
    }
}
