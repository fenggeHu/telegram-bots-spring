package tlg.bot.handler;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理用户消息
 * @author max.hu  @date 2024/12/04
 **/
public class UserMessageHandler extends InteractiveConsumeHandler {
    private final Map<Object, Method> methods = new ConcurrentHashMap<>();

    @Override
    public boolean matched(Update update) {
        return (update.hasMessage() || update.hasEditedMessage()) && update.getMessage().isUserMessage();
    }

    @Override
    @SneakyThrows
    public boolean execute(Update update, Object owner) {
        Method method = getHandleMethod(owner, "userMessageHandler", Update.class);
        method.invoke(owner, update);

        return false;
    }

}
