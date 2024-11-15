package tlg.bot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import tool.internal.cmd.CmdClass;
import tool.internal.cmd.CmdMethod;
import tool.internal.cmd.CmdMethodKeeper;
import tool.internal.cmd.ExtClass;

import java.util.Collection;

/**
 * 构建cmd代理类和相关Extension类，注册到spring bean
 *
 * @author max.hu  @date 2024/11/07
 **/
@Slf4j
@Component("cmdBeanFactory")
public class CmdBeanFactory implements ApplicationContextAware {

    // 使用构造参数初始化main class
    public static CmdClass registerCmdBean(final CmdClass cmdClass, Object... constructorArgs) {
        // 加载类信息
        CmdMethodKeeper.set(cmdClass.getId(), cmdClass);  // 统一管理
        // 注册boot bean
        var boot = registerBean(cmdClass.getId(), cmdClass.getCmdClass(), constructorArgs);
        cmdClass.setBase(boot);
        // 更新keeper
        fillKeeper(cmdClass.getId(), cmdClass.getMethods().values(), cmdClass.getClazz(), boot);

        // 注册ext bean
        for (ExtClass ext : cmdClass.getExtClass()) {
            ext.setId(cmdClass.getId() + "_" + ext.getId());
            var eb = registerBean(ext.getId(), ext.getCmdClass());
            // 补充Ext属性值
            ext.fillExtFieldValue(boot, eb);
            // 更新keeper
            fillKeeper(cmdClass.getId(), cmdClass.getMethods().values(), ext.getClazz(), eb);
        }

        return cmdClass;
    }

    public static CmdClass registerCmdBean(String rootId, Class<?> baseClass, final String[] extClassName, String[] extPackage,
                                           Object... constructorArgs) {
        // 加载类信息
        CmdClass cmdClass = CmdClass.of(rootId, baseClass, extClassName, extPackage);
        return registerCmdBean(cmdClass, constructorArgs);
    }

    private static void fillKeeper(String rootId, Collection<CmdMethod> cms, Class<?> clazz, Object obj) {
        // 补充对象信息
        for (CmdMethod cm : cms) {
            if (cm.getClazz() == clazz) {
                cm.setCmdObject(obj);
                // 记录类名
                CmdMethodKeeper.addInstance(rootId, obj);
            }
        }
    }

    public static Object registerBean(String beanName, Class<?> clazz, Object... constructorArgs) {
        if (null == constructorArgs || constructorArgs.length == 0) {
            applicationContext.registerBean(beanName, clazz);
        } else {
            try {
                applicationContext.registerBean(beanName, clazz, constructorArgs);
            } catch (Exception e) {
                log.error("Register Spring Bean with constructor failed. trying it without constructor");
                applicationContext.registerBean(beanName, clazz);
            }
        }
        return applicationContext.getBean(beanName);
    }

    public static Object getBean(String beanId) {
        return applicationContext.getBean(beanId);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static <T> T getBean(String beanId, Class<T> requiredType) {
        return applicationContext.getBean(beanId, requiredType);
    }

    private static AnnotationConfigApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        if (null == applicationContext) {
            applicationContext = (AnnotationConfigApplicationContext) ctx;
        }
    }
}
