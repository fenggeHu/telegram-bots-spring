package tool.internal.cmd;

import lombok.extern.slf4j.Slf4j;
import tool.utils.ClassUtil;
import tool.utils.ReflectionUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

/**
 * 能力扩展
 * 1，用于类：扩展baseClass的能力
 * 2，用于属性：注入扩展类(baseClass)的属性
 *
 * @author max.hu  @date 2024/11/07
 **/
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ext {
    /**
     * 唯一标识ID
     * 使用spring时被注册为bean id/name
     */
    String value() default "";

    /**
     * 被扩展的类
     */
    Class<?>[] baseClass() default {};

    /**
     * 注解转extension class information
     */
    @Slf4j
    class Builder {
        // 查询类的Ext注解信息
        public static ExtClass of(Class<?> clazz) {
            // 类注解
            Ext ext = clazz.getAnnotation(Ext.class);
            if (null == ext) return null;
            var id = ext.value().length() > 0 ? ext.value().trim() : clazz.getSimpleName();

            // 属性注解 -- 包含了父类的属性注解
            var fields = ClassUtil.getDeclaredFieldsWithAnnotation(clazz, Ext.class);

            return ExtClass.builder().id(id).clazz(clazz).baseClass(ext.baseClass()).extFields(fields).build();
        }

        // 如果clazz没有Ext注解信息，就补充baseClass
        public static ExtClass of(Class<?> baseClass, Class<?> extClass) {
            ExtClass extInfo = of(extClass);
            if (null == extInfo) {
                extInfo = ExtClass.builder().id(extClass.getName()).build();
            }
            if (extInfo.getBaseClass() == null) {
                extInfo.setBaseClass(new Class[]{baseClass});
            }
            return extInfo;
        }

        /**
         * 要保证顺序
         */
        public static List<ExtClass> of(Class<?> baseClass, String... extClass) {
            Set<String> keys = new HashSet<>();
            List<ExtClass> extClasses = new LinkedList<>();
            for (String cs : extClass) {
                var clazz = ClassUtil.getClass(cs);
                var ext = Builder.of(baseClass, clazz);
                if (keys.contains(ext.getId())) {
                    log.info("{} Ext Class Id has already exists. Ignored: {}", ext.getClazz().getName(), ext.getId());
                    continue;
                }
                keys.add(ext.getId());
                extClasses.add(ext);
            }
            return extClasses;
        }

        public static Map<String, ExtClass> of(String... pkgs) {
            Map<String, ExtClass> extClasses = new HashMap<>();
            var extClassSet = ReflectionUtil.getClassWithAnnotation(Ext.class, pkgs);
            for (Class<?> clazz : extClassSet) {
                var ext = Builder.of(clazz);
                if (extClasses.containsKey(ext.getId())) {
                    log.info("{} Ext Class Id has already exists. Ignored: {}", ext.getClazz().getName(), ext.getId());
                    continue;
                }
                extClasses.put(ext.getId(), ext);
            }
            return extClasses;
        }
    }
}
