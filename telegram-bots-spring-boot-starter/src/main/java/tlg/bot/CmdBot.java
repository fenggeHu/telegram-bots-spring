package tlg.bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import tlg.bot.entity.Command;
import tlg.bot.entity.Config;
import tool.internal.cmd.Cmd;
import tool.internal.cmd.CmdDTO;

/**
 * @author max.hu  @date 2024/11/13
 **/
@Slf4j
public class CmdBot extends BotWriter {
    public CmdBot(Config config) {
        super(config);
    }

    // 需要实现
    public void handleMessage(final Update update) {
        Message message = update.getMessage();
        if (message.isCommand()) {
            this.doCommand(update);
        } else if (message.isUserMessage()) {
            this.doUserMessage(message);
        } else if (message.isChannelMessage()) {
            log.info("channel message: {}", message.getText());
        } else if (message.isGroupMessage()) {
            log.info("group message: {}", message.getText());
        } else if (message.isTopicMessage()) {
            log.info("topic message: {}", message.getText());
        } else if (message.isReply()) {
            log.info("reply: {}", message.getText());
        } else if (message.isSuperGroupMessage()) {
            log.info("super group message: {}", message.getText());
        } else {
            log.info("unknown: {}", message.getText());
        }
    }

    protected void handleEditedMessage(final Update update) {
        log.info("It's EditedMessage");
        this.handleMessage(update);
    }

    protected void doUserMessage(final Message message) {
        log.info("user message: {}", message.getText());
    }

    // 处理命令
    @Cmd
    public CmdDTO doCommand(final Update update) {
        var cmd = Command.of(update);
        return CmdDTO.of(cmd.getExe(), cmd);
    }

    /**
     * 使用start做链接跳转
     * // 经过测试：1，只有start成功；2，那么，经过start再跳转。
     * // 3，经过start跳转到command；4，command字符串支持字母、数字、横杠-、下划线_、等号=
     * // 结论 - 定义规则：使用kv表达command，用下划线代表空格
     *
     * @param command
     */
    @Cmd
    public CmdDTO start(Command command) {
        if (StringUtils.isNoneBlank(command.getParameter())) {
            var cmd = jump(command.getParameter());
            cmd.setChatId(cmd.getChatId());
            cmd.setUpdate(command.getUpdate());
            return CmdDTO.of(cmd.getExe()).putArgs(cmd);
        }

        return null;
    }

    // 按照规则解析跳转
    private Command jump(String param) {
        // 1,先取得命令
        int index = param.indexOf("=");
        if (index <= 0) {
            return Command.of(param, null);
        }
        // 解析参数
        String cmd = param.substring(0, index);
        return Command.of(cmd, param.substring(index + 1));
    }
}
