package tlg.bot.handler;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;
import tlg.bot.entity.Context;

import java.lang.reflect.Method;

/**
 * 处理命令
 *
 * @author max.hu  @date 2024/12/04
 **/
public class CommandHandler extends InteractiveConsumeHandler {

    @Override
    public boolean matched(Update update) {
        return (update.hasMessage() || update.hasEditedMessage()) && update.getMessage().isCommand();
    }

    @Override
    @SneakyThrows
    public boolean execute(Context ctx) {
        Method method = getHandleMethod(ctx.getOwner(), "commandHandler", Update.class);
        method.invoke(ctx.getOwner(), ctx.getUpdate());

        return false;
    }

}
