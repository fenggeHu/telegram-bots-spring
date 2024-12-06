package tlg.bot.entity;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

/**
 * @author max.hu  @date 2024/12/05
 **/
@Getter
public class Context {
    // 信息
    private final Update update;
    // 所属的bot实例
    private final Object owner;
    // 放一些上下文的临时数据 - 方便多个处理器联合工作
    private final Map<String, Object> data = new HashMap<>();

    public Context(Update update, Object owner) {
        this.update = update;
        this.owner = owner;
    }

    public static Context of(Update update, Object owner) {
        return new Context(update, owner);
    }

    public Object get(String key) {
        return data.get(key);
    }
}
