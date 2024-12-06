package tlg.bot.handler;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;
import tlg.bot.entity.Context;

import java.lang.reflect.Method;

/**
 * 处理用户消息
 *
 * @author max.hu  @date 2024/12/04
 **/
public class UserMessageHandler extends InteractiveConsumeHandler {
    public final static String handlerMethodName = "userMessageHandler";

    @Override
    public boolean matched(Update update) {
        return (update.hasMessage() || update.hasEditedMessage()) && update.getMessage().isUserMessage();
    }

    @Override
    @SneakyThrows
    public boolean execute(Context ctx) {
        Method method = getHandlerMethod(ctx.getOwner(), handlerMethodName, Update.class);
        method.invoke(ctx.getOwner(), ctx.getUpdate());

        return false;
    }

}
