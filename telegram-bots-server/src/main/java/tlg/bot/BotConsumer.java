package tlg.bot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import tlg.bot.entity.Config;

/**
 * Get Chat Update - 读取信息的Bot - 封装公共逻辑
 * https://rubenlagus.github.io/TelegramBotsDocumentation/telegram-bots.html
 *
 * @author max.hu  @date 2024/10/24
 **/
@Slf4j
public abstract class BotConsumer implements LongPollingSingleThreadUpdateConsumer {
    protected static final ObjectMapper mapper = new ObjectMapper()
            // 设置在反序列化时忽略在JSON字符串中存在，而在Java中不存在的属性
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Getter
    @Setter
    protected Config config;

    public BotConsumer() {
    }

    /**
     * 通过配置初始化相关的信息
     */
    public BotConsumer(Config config) {
        this.config = config;
    }

    // 处理逐条消息
    @Override
    @SneakyThrows
    public void consume(Update update) {
        if (log.isDebugEnabled()) {
            log.debug("consume Update:\n{}", mapper.writeValueAsString(update));
        }
        if (update.hasMessage()) {  // 消息：文字和图片等
            this.handleMessage(update);
        } else if (update.hasEditedMessage()) {  // 修改消息：文字和图片等
            this.handleEditedMessage(update);
        } else if (update.hasCallbackQuery()) { // 交互Callback
            this.handleCallbackQuery(update);
        } else if (update.hasPoll()) { // 投票
            log.warn("Poll: {}", update);
        } else {
            // ?
            log.error("FIXME consume: {}", update);
        }
    }

    public void handleMessage(final Update update) {
    }

    protected void handleEditedMessage(final Update update) {
    }

    protected void handleCallbackQuery(final Update update) {
    }

}
