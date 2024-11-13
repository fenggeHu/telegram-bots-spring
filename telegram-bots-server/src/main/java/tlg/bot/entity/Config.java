package tlg.bot.entity;

import lombok.Data;

/**
 * @author max.hu  @date 2024/10/24
 **/
@Data
public class Config {
    // telegram username - 全局唯一
    private String id;
    // Bot view name
    private String name;
    private String token;
    // Bot实现类
    private String botClassName;
}
