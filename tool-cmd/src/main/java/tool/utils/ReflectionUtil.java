package tool.utils;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import static org.reflections.scanners.Scanners.TypesAnnotated;

/**
 * @author max.hu  @date 2024/11/06
 **/
public class ReflectionUtil {

    // 查询包及子包下所有带注解的类
    public static Set<Class<?>> getClassWithAnnotation(String pkg, Class<? extends Annotation> annotationClass) {
        Reflections reflections = new Reflections(pkg);
        return reflections.get(TypesAnnotated.with(annotationClass).asClass());
    }
    // 查询多个包及子包下所有带注解的类
    public static Set<Class<?>> getClassWithAnnotation(Class<? extends Annotation> annotationClass, String... pkgs) {
        Set<Class<?>> ret = new HashSet<>();
        for (String pkg : pkgs) {
            ret.addAll(getClassWithAnnotation(pkg, annotationClass));
        }
        return ret;
    }
}
