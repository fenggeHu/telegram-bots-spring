package tlg.bot.handler;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 需要与对象交互的ConsumeHandler
 *
 * @author max.hu  @date 2024/12/04
 **/
public class InteractiveConsumeHandler implements ConsumeHandler {
    //缓存： object -> methodName -> method：使用时注意同一个对象的同名方法只能有一个
    private final Map<Object, Map<String, Method>> methods = new ConcurrentHashMap<>();

    @Override
    public boolean matched(Update update) {
        return false;
    }

    @Override
    public boolean execute(Update update, Object owner) {
        return false;
    }

    protected Method getHandleMethod(Object owner, String methodName, Class<?>... parameterTypes) {
        Map<String, Method> map = methods.computeIfAbsent(owner, e -> new ConcurrentHashMap<>());
        return map.computeIfAbsent(methodName, e -> getMethod(owner, methodName, parameterTypes));
    }

    // Declared Method：含父类所有的public方法
    @SneakyThrows
    private Method getMethod(Object owner, String methodName, Class<?>... parameterTypes) {
        return owner.getClass().getDeclaredMethod(methodName, parameterTypes);
    }
}
