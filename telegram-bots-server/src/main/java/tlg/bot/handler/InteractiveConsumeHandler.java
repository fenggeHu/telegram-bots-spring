package tlg.bot.handler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tlg.bot.entity.Context;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 需要与owner对象交互的ConsumeHandler
 *
 * @author max.hu  @date 2024/12/04
 **/
@Slf4j
public class InteractiveConsumeHandler implements ConsumeHandler {
    //缓存： object -> methodName -> method：使用时注意同一个对象的同名方法只能有一个
    private final Map<Object, Map<String, Method>> methods = new ConcurrentHashMap<>();

    @Override
    public boolean matched(Update update) {
        return false;
    }

    @Override
    public boolean execute(Context ctx) {
        return false;
    }

    protected Method getHandlerMethod(Object owner, String methodName, Class<?>... parameterTypes) {
        Map<String, Method> map = methods.computeIfAbsent(owner, e -> new ConcurrentHashMap<>());
        return map.computeIfAbsent(methodName, e -> getMethod(owner, methodName, parameterTypes));
    }

    @SneakyThrows
    private Method getMethod(Object owner, String methodName, Class<?>... parameterTypes) {
        return this.getDeclaredMethod(owner.getClass(), methodName, parameterTypes);
    }

    // 查找第一个匹配的方法 - 从子类逐步往父类找
    @SneakyThrows
    public static Method getDeclaredMethod(Class clazz, String name, Class<?>... parameterTypes) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(name, parameterTypes);
            log.info("Get Method: {}.{}", clazz.getName(), name);
            return method;  // clazz.getDeclaredMethod 返回值不为空
        } catch (Exception e) {
            log.info("No Method: {}.{}", clazz.getName(), name);
        }
        Class sc = clazz.getSuperclass();
        if (null != sc) {
            method = getDeclaredMethod(sc, name, parameterTypes);
        }
        return method;
    }
}
