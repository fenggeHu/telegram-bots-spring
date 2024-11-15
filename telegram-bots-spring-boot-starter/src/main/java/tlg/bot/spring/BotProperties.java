package tlg.bot.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tlg.bot.entity.Config;
import tlg.bot.ext.BotConfig;
import tlg.bot.ext.ProxyConfig;

import java.util.List;

/**
 * Bot配置项
 *
 * @author max.hu  @date 2024/10/24
 **/

@Data
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
    // 网络代理
    private ProxyConfig proxy;
    // bot config
    private List<BotConfig> configs;

    public Config byToken(String token) {
        return configs.stream().filter(e -> token.equals(e.getToken())).findFirst().orElse(null);
    }

    public Config byId(String id) {
        return configs.stream().filter(e -> id.equals(e.getId())).findFirst().orElse(null);
    }

}
