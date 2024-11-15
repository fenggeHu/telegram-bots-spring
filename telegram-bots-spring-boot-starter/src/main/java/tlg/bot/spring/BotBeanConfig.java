package tlg.bot.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.Authenticator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;
import tlg.bot.ext.BotConfig;
import tool.internal.cmd.CmdClass;
import tool.internal.cmd.CmdMethodKeeper;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.Map;

/**
 * @author max.hu  @date 2024/11/15
 **/
@Configuration("botBeanConfig")
@DependsOn("cmdBeanFactory")
public class BotBeanConfig {

    @Bean
    public TelegramBotsLongPollingApplication botsApplication(BotProperties botProperties) {
        // 没有配置代理或者代理参数缺失
        if (null == botProperties.getProxy() || StringUtils.isBlank(botProperties.getProxy().getHost())
                || StringUtils.isBlank(botProperties.getProxy().getType()) || botProperties.getProxy().getPort() <= 0) {
            return new TelegramBotsLongPollingApplication();
        } else {
            return new TelegramBotsLongPollingApplication(ObjectMapper::new,
                    new TelegramOkHttpClientFactory.HttpProxyOkHttpClientCreator(
                            () -> new Proxy(Proxy.Type.valueOf(botProperties.getProxy().getType()),
                                    new InetSocketAddress(botProperties.getProxy().getHost(), botProperties.getProxy().getPort())),
                            () -> Authenticator.NONE
                    ));
        }
    }

    // 为了使用Spring的相关能力，先把Bot注册成Spring Bean，再注册成TelegramBots
    @Bean
    public Map<String, CmdClass<?>> registerBean(BotProperties botProperties) {
        var configs = botProperties.getConfigs();
        if (null == configs) {
            return Collections.emptyMap();
        }
        configs.parallelStream().forEach(e -> registerBean(e));
        return Collections.unmodifiableMap(CmdMethodKeeper.getBootClasses());
    }

    @SneakyThrows
    private CmdClass registerBean(final BotConfig config) {
        //boot class
        Class<?> bootClass = Class.forName(config.getBotClassName());
        // 方法一：使用cmd注解实现逻辑
        return CmdBeanFactory.registerCmdBean(config.getId(), bootClass, config.getExtClass(), config.getExtPackage(), config);
        // 方法二：不使用cmd的方式
//        try {
//            registerBean(config.getId(), bootClass, config);
//        } catch (Exception e) {
//            registerBean(config.getId(), bootClass);
//        }

    }
}
