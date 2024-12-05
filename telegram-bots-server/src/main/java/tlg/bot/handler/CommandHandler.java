package tlg.bot.handler;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;

/**
 * 处理命令
 * @author max.hu  @date 2024/12/04
 **/
public class CommandHandler extends InteractiveConsumeHandler {

    @Override
    public boolean matched(Update update) {
        return (update.hasMessage() || update.hasEditedMessage()) && update.getMessage().isCommand();
    }

    @Override
    @SneakyThrows
    public boolean execute(Update update, Object owner) {
        Method method = getHandleMethod(owner, "commandHandler", Update.class);
        method.invoke(owner, update);

        return false;
    }

}
