package tlg.bot.entity;

import lombok.Getter;

/**
 * 正常情况下初始化后不允许修改
 *
 * @author max.hu  @date 2024/10/24
 **/
@Getter
public class Config {
    // telegram username - 全局唯一
    private String id;
    // Bot view name
    private String name;
    private String token;
    // Bot实现类
    private String botClassName;
    // ConsumeHandler - 配置消费处理类名
    private String[] consumes;

    // setter
    public void setId(String id) {
        if (this.id == null) this.id = id;
    }

    public void setName(String name) {
        if (this.name == null) this.name = name;
    }

    public void setToken(String token) {
        if (this.token == null) this.token = token;
    }

    public void setBotClassName(String className) {
        if (this.botClassName == null) this.botClassName = className;
    }

    public void setConsumes(String[] consumes) {
        if (this.consumes == null) this.consumes = consumes;
    }

    // deep linking
    // Each bot has a link that opens a conversation with it in Telegram – https://t.me/<bot_username>
    // Private Chats: https://t.me/your_bot?start=command  ==> /start command
    // 经过测试：1，只有start成功；2，那么，经过start再跳转。
    // 3，经过start跳转到command；4，command字符串支持字母、数字、横杠-、下划线_、等号=
    // Groups: https://t.me/your_bot?startgroup=spaceship   ==> /start@your_bot spaceship
    public String botLink() {
        return "https://t.me/" + this.id;
    }
}
