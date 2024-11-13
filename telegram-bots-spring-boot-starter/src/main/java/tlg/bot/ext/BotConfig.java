package tlg.bot.ext;

import lombok.Data;
import tlg.bot.entity.Config;

/**
 * @author max.hu  @date 2024/11/13
 **/
@Data
public class BotConfig extends Config {
    // 扩展类
    private String[] extClass;
    // 扩展包
    private String[] extPackage;
}
