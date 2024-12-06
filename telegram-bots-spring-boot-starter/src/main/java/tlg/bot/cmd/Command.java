package tlg.bot.cmd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * /command text
 *
 * @author max.hu  @date 2024/10/28
 **/
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Command {
    public Long chatId;
    // 指令exe /command
    public String exe;
    // 参数 xxx
    public String parameter;

    // 收到的完整信息
    public Update update;

    public Message message() {
        return this.update.getMessage();
    }

    public Chat chat() {
        return message().getChat();
    }

    // 解析parameter - 空格分隔
    public String[] args() {
        return this.parameter.split(" ");
    }

    // 解析parameter。 key：--key， kv空格分隔
    // 格式： -x 124 -y 4354 -z -a hello
    private static final Pattern pattern = Pattern.compile("-([a-zA-Z0-9-]+)(?:\\s+([^\s-]+))?");

    public Map<String, String> toArgs() {
        Map<String, String> params = new HashMap<>();
        if (StringUtils.isBlank(this.parameter)) {
            return params;
        }
        Matcher matcher = pattern.matcher(this.parameter.trim());
        while (matcher.find()) {
            String key = matcher.group(1);
            if (key.startsWith("-")) {
                key = key.substring(1);
            }
            String value = matcher.group(2);
            params.put(key, value);
        }

        return params;
    }

    /**
     * 从message提取第一个命令。
     * - 这里只取第1个entity是指令的转成指令和参数
     * - Message.isCommand()已经保证第1个是command
     */
    public static Command of(final Message message) {
        var entity = message.getEntities().get(0);
        var text = message.getText();

        if (entity.getLength() == text.length()) {  // 只有命令没有参数
            return Command.builder().chatId(message.getChatId()).exe(text).build();
        } else {    // 命令+后面的当成参数
            return Command.builder().chatId(message.getChatId())
                    .exe(text.substring(entity.getOffset(), entity.getLength()))
                    .parameter(text.substring(entity.getLength() + 1))
                    .build();
        }
    }

    public static Command of(final Update update) {
        var cmd = of(update.getMessage());
        cmd.setUpdate(update);
        return cmd;
    }

    public static Command of(String exe, String parameter) {
        return Command.builder().exe(exe).parameter(parameter).build();
    }
}
