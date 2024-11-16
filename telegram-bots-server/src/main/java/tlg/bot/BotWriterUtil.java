package tlg.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * 服务端输出到Telegram bot的操作工具
 *
 * @author max.hu  @date 2024/11/12
 **/
@Slf4j
public class BotWriterUtil {

    // write text
    public static void text(final TelegramClient telegramClient, Long chatId, String txt) {
        if (StringUtils.isBlank(txt.trim())) return;    // api - 不能发送空消息
        var sm = SendMessage.builder().chatId(chatId).text(txt).build();
        try {
            // Execute it
            telegramClient.execute(sm);
        } catch (TelegramApiException e) {
            log.error("write to tlg", e);
        }
    }

    @SneakyThrows
    public static void message(final TelegramClient telegramClient, SendMessage sendMessage) {
        telegramClient.execute(sendMessage);
    }
}
