package tlg.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
        sendMessage(telegramClient, sm);
    }

    // write markdown
    public static void markdown(final TelegramClient telegramClient, Long chatId, String md) {
        if (StringUtils.isBlank(md.trim())) return;    // api - 不能发送空消息
        var sm = SendMessage.builder().chatId(chatId).parseMode(ParseMode.MARKDOWN).text(md).build();
        sendMessage(telegramClient, sm);
    }

    public static void markdownV2(final TelegramClient telegramClient, Long chatId, String md) {
        if (StringUtils.isBlank(md.trim())) return;    // api - 不能发送空消息
        var sm = SendMessage.builder().chatId(chatId).parseMode(ParseMode.MARKDOWNV2)
                .text(escapeMarkdownV2(md)).build();
        sendMessage(telegramClient, sm);
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

    // write html - 仅支持以下格式
    //<b>bold</b>, <strong>bold</strong>
    //<i>italic</i>, <em>italic</em>
    //<u>underline</u>, <ins>underline</ins>
    //<s>strikethrough</s>, <strike>strikethrough</strike>, <del>strikethrough</del>
    //<span class="tg-spoiler">spoiler</span>, <tg-spoiler>spoiler</tg-spoiler>
    //<b>bold <i>italic bold <s>italic bold strikethrough <span class="tg-spoiler">italic bold strikethrough spoiler</span></s> <u>underline italic bold</u></i> bold</b>
    //<a href="http://www.example.com/">inline URL</a>
    //<a href="tg://user?id=123456789">inline mention of a user</a>
    //<tg-emoji emoji-id="5368324170671202286">👍</tg-emoji>
    //<code>inline fixed-width code</code>
    //<pre>pre-formatted fixed-width code block</pre>
    //<pre><code class="language-python">pre-formatted fixed-width code block written in the Python programming language</code></pre>
    //<blockquote>Block quotation started\nBlock quotation continued\nThe last line of the block quotation</blockquote>
    //<blockquote expandable>Expandable block quotation started\nExpandable block quotation continued\nExpandable block quotation continued\nHidden by default part of the block quotation started\nExpandable block quotation continued\nThe last line of the block quotation</blockquote>
    public static void html(final TelegramClient telegramClient, Long chatId, String html) {
        if (StringUtils.isBlank(html.trim())) return;    // api - 不能发送空消息
        var sm = SendMessage.builder().chatId(chatId).text(html).parseMode(ParseMode.HTML).build();
        sendMessage(telegramClient, sm);
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
    public static void sendMessage(final TelegramClient telegramClient, SendMessage sendMessage) {
        telegramClient.execute(sendMessage);
    }
}
