package tool.internal.cmd;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import tool.utils.ClassUtil;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 扩展类 信息
 *
 * @author max.hu  @date 2024/11/07
 **/
@Data
@Builder
public class ExtClass {
    // 唯一标识 - Ext注解的value，默认取类名
    protected String id;
    // 原类
    protected Class<?> clazz;
    // 通过ByteBuddy转换后的cmdClass
    protected Class<?> cmdClass;
    // extends
    protected Class<?>[] baseClass;
    // 实例化的对象
    private Object ext;
    // ext fields - 为了注入base类的属性
    protected List<Field> extFields;

    // 后置的处理
    public void postConstruct() {
        // TODO
    }

    // 补充扩展属性的值
    public void fillExtFieldValue(Object base, Object ext) {
        this.ext = ext;
        if (null == this.extFields) return;

        List<Field> baseClassFields = ClassUtil.getDeclaredFields(base.getClass());
        this.extFields.forEach(f -> {
            Class<?> fc = f.getType();
            if (null == ClassUtil.getFieldValue(ext, f)) {   // 如果为空-赋予baseClass实例的同名属性值
                baseClassFields.forEach(b -> {
                    // 判断boot field是否能赋值给ext field
                    if (b.getName().equals(f.getName()) && b.getType().isAssignableFrom(fc)) {
                        var value = ClassUtil.getFieldValue(base, b);
                        if (null != value) {
                            ClassUtil.silencedInjection(ext, f, value);
                        }
                    }
                });
            }
        });
    }

    // 查找原扩展类
    @SneakyThrows
    public static List<ExtClass> getExtClasses(Class<?> baseClass, final String[] extClass, String[] extPackage) {
        List<ExtClass> extClasses = new LinkedList<>();
        Set<String> keys = new HashSet<>();
        // 优先1：扩展类 - extClass
        if (null != extClass) {
            var ext = Ext.Builder.of(baseClass, extClass);
            extClasses.addAll(ext);
            keys.addAll(ext.stream().map(ExtClass::getId).collect(Collectors.toSet()));
        }

        // 优先2：扫描package。 包扫描判断Ext注解
        if (null != extPackage) {
            var pkgClass = Ext.Builder.of(extPackage);
            pkgClass.forEach((k, v) -> {
                if (!keys.contains(k)) {
                    for (Class<?> bc : v.getBaseClass()) {
                        if (bc == baseClass) {
                            extClasses.add(v);
                            keys.add(k);
                        }
                    }
                }
            });
        }

        return extClasses;
    }

}
