package tlg.bot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import tlg.bot.entity.Config;
import tlg.bot.handler.ConsumeHandler;

import java.util.LinkedList;
import java.util.List;

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
    // Consume handlers
    protected final List<ConsumeHandler> consumeHandlers = new LinkedList<>();
    @Getter
    protected Config config;

    @SneakyThrows
    public void setConfig(Config config) {
        this.config = config;
        if (null != config && config.getConsumes() != null) {
            this.consumeHandlers.clear();
            for (String cs : config.getConsumes()) {
                Class<?> clazz = Class.forName(cs);
                Object ch = clazz.getDeclaredConstructor().newInstance();
                this.consumeHandlers.add((ConsumeHandler) ch);
            }
        }
    }

    public BotConsumer() {
    }

    /**
     * 通过配置初始化相关的信息
     */
    public BotConsumer(Config config) {
        this.setConfig(config);
    }

    // 处理消息
    @Override
    @SneakyThrows
    public void consume(Update update) {
        for (ConsumeHandler ch : consumeHandlers) {
            if (ch.matched(update)) {
                log.debug("UpdateId={} matched and execute: {}", update.getUpdateId(), ch.getClass().getName());
                if (!ch.execute(update, this)) {
                    break;
                }
            }
        }
        log.debug("UpdateId={} consume finished.", update.getUpdateId());
    }

    public void userMessageHandler(Update update) {
        // default for UserMessageHandler
    }
}
