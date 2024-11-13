package tool.internal.cmd;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tool.internal.bb.ByteBuddyUtil;
import tool.internal.bb.InterceptConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一个完整的boot Class和其扩展类
 * boot class 也可以有cmd方法
 *
 * @author max.hu  @date 2024/11/07
 **/
@Slf4j
@Builder
public class CmdClass<T> {
    // 唯一ID - bean id/name
    @Getter
    private String id;
    // boot 对象实例id
    // boot class 原类
    @Getter
    private Class<T> clazz;
    // boot base class cmd拦截类或者等于clazz
    @Getter
    private Class<?> cmdClass;
    @Getter
    private ExtClass[] extClass;
    // ===对象的实例===
    // base class实例化的对象
    @Setter
    @Getter
    private T base;

    // 所有含注解的method信息 - cmd id/value -> Method
    @Getter
    private final Map<String, CmdMethod> methods = new HashMap<>();

    public void putMethod(String mid, CmdMethod cmi) {
        this.methods.put(mid, cmi);
    }

    public CmdMethod<?> getMethod(String mid) {
        return this.methods.get(mid);
    }

    // 构建Cmd Class信息
    public static <T> CmdClass<?> of(String id, Class<T> baseClass, String[] extClasses, String[] extPackage) {
        var extClass = ExtClass.getExtClasses(baseClass, extClasses, extPackage);
        return of(id, baseClass, extClass.toArray(new ExtClass[0]));
    }

    // 构建基类 - 处理Cmd注解
    @SneakyThrows
    public static <T> CmdClass<?> of(String id, Class<T> baseClass, ExtClass... extClass) {
        CmdClass<?> cmdClass = CmdClass.<T>builder().id(id).clazz(baseClass).build();

        //1, 先加载boot class
        var bootCmdMethods = CmdMethod.getCmdMethod(baseClass, true);
        if (bootCmdMethods.isEmpty()) {
            cmdClass.cmdClass = baseClass;
        } else {  // 需要注入Cmd拦截器
            // 记录扩展类的信息
            Method[] cms = bootCmdMethods.values().stream().map(CmdMethod::getMethod).distinct().toArray(Method[]::new);
            var ic = InterceptConfig.builder().id(id).clazz(baseClass)
                    .build();
            ic.addMethod(CmdInterceptor.class, cms);
            cmdClass.cmdClass = ByteBuddyUtil.buildClass(ic);
            CmdMethod.fillCmdMethod(bootCmdMethods, cmdClass.getCmdClass());
            cmdClass.methods.putAll(bootCmdMethods);
        }
        //2, 分别处理ext class
        List<ExtClass> validExtClass = new LinkedList<>();    // 记录有效的ext class
        Map<String, CmdMethod<?>> validExtCmdMethod = new HashMap<>();
        for (ExtClass ext : extClass) {
            var cmdMethod = CmdMethod.getCmdMethod(ext.getClazz(), true);
            if (!cmdMethod.isEmpty()) {
                // cmd method
                List<CmdMethod> validCmdMethods = new LinkedList<>();  // 记录有效的cmd method
                for (Map.Entry<String, CmdMethod> entry : cmdMethod.entrySet()) {
                    var k = entry.getKey();
                    var v = entry.getValue();
                    var mtd = cmdClass.getMethod(k);
                    if (null == mtd || mtd.getClazz() == baseClass) {   // 扩展类可以替换基类
                        cmdClass.putMethod(k, v);
                        validCmdMethods.add(v);
                        validExtCmdMethod.put(k, v);
                    } else {
                        log.warn("{} cmd method exists {}. method value: {}",
                                v.getMethod().getName(), mtd.getMethod().getName(), k);
                    }
                }
                if (!validCmdMethods.isEmpty()) {
                    Method[] cms = validCmdMethods.stream().map(e -> e.getMethod()).collect(Collectors.toSet()).toArray(new Method[0]);
                    // 扩展类的拦截有效的Cmd
                    var ic = InterceptConfig.builder().id(ext.getId()).clazz(ext.getClazz())
                            .build();
                    ic.addMethod(CmdInterceptor.class, cms);
                    var extCmdClass = ByteBuddyUtil.buildClass(ic);
                    CmdMethod.fillCmdMethod(validCmdMethods, extCmdClass);
                    ext.setCmdClass(extCmdClass);
                    validCmdMethods.forEach(e -> e.setCmdClass(extCmdClass));
                    validExtClass.add(ext);
                }
            }
        }

        //3, 只记入有效的ext Class
        cmdClass.extClass = validExtClass.toArray(new ExtClass[0]);

        return cmdClass;
    }
}
