package tlg.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import tlg.bot.entity.Config;

/**
 * Send Message to Chat - 发送信息
 *
 * @author max.hu  @date 2024/10/28
 **/
@Slf4j
public class BotWriter extends BotConsumer {
    @Getter
    @Setter
    protected TelegramClient telegramClient;

    public BotWriter() {
    }

    /**
     * 通过配置初始化相关的信息
     *
     * @param config
     */
    public BotWriter(Config config) {
        super(config);
        this.initTelegramClient();
    }

    // 初始化TelegramClient
    public void initTelegramClient() {
        telegramClient = new OkHttpTelegramClient(new OkHttpClient.Builder().build(),
                config.getToken(), TelegramUrl.DEFAULT_URL);
    }

    // write text
    protected void writeText(Long chatId, String txt) {
        if (StringUtils.isBlank(txt.trim())) return;    // api - 不能发送空消息
        var sm = SendMessage.builder().chatId(chatId).text(txt).build();
        try {
            // Execute it
            telegramClient.execute(sm);
        } catch (TelegramApiException e) {
            log.error("write to tlg", e);
        }
    }
}
