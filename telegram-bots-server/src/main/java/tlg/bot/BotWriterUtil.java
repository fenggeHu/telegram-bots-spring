package tlg.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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

    // write markdown
    public static void markdown(final TelegramClient telegramClient, Long chatId, String md) {
        if (StringUtils.isBlank(md.trim())) return;    // api - 不能发送空消息
        var sm = SendMessage.builder().chatId(chatId).text(md).parseMode(ParseMode.MARKDOWN).build();
        try {
            // Execute it
            telegramClient.execute(sm);
        } catch (TelegramApiException e) {
            log.error("write to tlg", e);
        }
    }

    // 使用MarkdownV2时需要转码
    private static String escapeMarkdownV2(String text) {
        return text
                .replace("_", "\\_")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("#", "\\#")
                .replace(".", "\\.")
                .replace("*", "\\*")
                .replace("=", "\\=")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("|", "\\|")
                .replace("!", "\\!");
    }

    // write html
    public static void html(final TelegramClient telegramClient, Long chatId, String html) {
        if (StringUtils.isBlank(html.trim())) return;    // api - 不能发送空消息
        var sm = SendMessage.builder().chatId(chatId).text(html).parseMode(ParseMode.HTML).build();
        try {
            // Execute it
            telegramClient.execute(sm);
        } catch (TelegramApiException e) {
            log.error("write to tlg", e);
        }
    }

    // InputStream
//    public static void file(final TelegramClient telegramClient, Long chatId, InputStream is, String fileName) {
//        InputFile inputFile = new InputFile(is, fileName);
//        var sm = SendPhoto.builder().chatId(chatId).photo(inputFile).build();
//        try {
//            // Execute it
//            telegramClient.execute(sm);
//        } catch (TelegramApiException e) {
//            log.error("write to tlg", e);
//        }
//    }

//    public static void file(final TelegramClient telegramClient, Long chatId, File file) {
//        InputFile inputFile = new InputFile(file);
//        var sm = SendPhoto.builder().chatId(chatId).photo(inputFile).build();
//        try {
//            // Execute it
//            telegramClient.execute(sm);
//        } catch (TelegramApiException e) {
//            log.error("write to tlg", e);
//        }
//    }

    @SneakyThrows
    public static void message(final TelegramClient telegramClient, SendMessage sendMessage) {
        telegramClient.execute(sendMessage);
    }
}
