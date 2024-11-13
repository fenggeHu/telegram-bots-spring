package tlg.bot.spring;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.DefaultGetUpdatesGenerator;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tlg.bot.entity.Config;
import tlg.bot.BotConsumer;
import tlg.bot.BotWriter;
import tlg.bot.ext.BotConfig;

import java.util.List;

/**
 * 集中管理Bot及状态
 *
 * @author max.hu  @date 2024/10/24
 **/
@Slf4j
@Component
@DependsOn("cmdBeanFactory")
public class BotServerStarter implements CommandLineRunner {
    @Autowired
    private BotProperties botProperties;
    @Setter
    private int tryTimes = 3;
    final TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();

    // Instantiate Telegram Bots API
    @Override
    public void run(String... args) {
        // 登录Telegram Bots
        this.register(botProperties.getConfigs());
    }

    // 为了使用Spring的相关能力，先把Bot注册成Spring Bean，再注册成TelegramBots
    @Bean
    public void registerBean() {
        var configs = botProperties.getConfigs();
        if (null == configs) return;
        configs.parallelStream().forEach(e -> registerBean(e));
    }

    public void register(List<? extends Config> configs) {
        if (null == configs) return;
        configs.parallelStream().forEach(e -> register(e));
    }

    // 高可用    TODO 状态检测： 还要解决断线重连 -- 通过定时任务扫描TelegramBotsLongPollingApplication.isRunning
    // 1，模拟断开网络：BotSession支持超时重试。本地测试可用
    public BotSession register(final Config config) {
        var botName = config.getName();
        for (int i = 0; i < tryTimes; i++) {
            try {
                var bot = this.getBotBean(config);
                // 连接Telegram API，连接成功-BotSession
                BotSession session = botsApplication.registerBot(config.getToken(),
                        () -> TelegramUrl.DEFAULT_URL, new DefaultGetUpdatesGenerator(), bot);
                log.info("register Bot {}, Running={}", botName, session.isRunning());
                return session;
            } catch (Exception e) {
                log.error(botName + " Register Bot", e);
                try {
                    botsApplication.unregisterBot(config.getToken());
                } catch (TelegramApiException ex) {
                    log.error("Try unregisterBot: {}", botName, ex);
                }
                this.sleep(5000);
            }
        }
        return null;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            log.error("Try Register Bot: Thread Sleep Err", e);
        }
    }

    // TODO 待优化
    @SneakyThrows
    public void registerBean(final BotConfig config) {
        //boot class
        Class<?> bootClass = Class.forName(config.getBotClassName());
        // 不使用cmd的方式
//        try {
//            registerBean(config.getId(), bootClass, config);
//        } catch (Exception e) {
//            registerBean(config.getId(), bootClass);
//        }

        // 使用cmd注解实现逻辑
        CmdBeanFactory.registerCmdBean(config.getId(), bootClass, config.getExtClass(), config.getExtPackage(), config);
    }

    @SneakyThrows
    private BotConsumer getBotBean(final Config config) {
        var bot = CmdBeanFactory.getBean(config.getId());
        if (BotWriter.class.isInstance(bot)) {
            var bw = (BotWriter) bot;
            if (bw.getConfig() == null) {
                bw.setConfig(config);
            }
            if (bw.getTelegramClient() == null) {
                bw.initTelegramClient();
            }
        } else if (BotConsumer.class.isInstance(bot)) {  // 判断bot config是否被注入
            var br = (BotConsumer) bot;
            if (br.getConfig() == null) {
                br.setConfig(config);
            }
        }
        return (BotConsumer) bot;
    }

}
