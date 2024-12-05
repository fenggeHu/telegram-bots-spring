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
    private final Update update;
    private final Object owner;
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
