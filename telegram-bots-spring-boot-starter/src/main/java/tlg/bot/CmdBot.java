package tlg.bot;

import lombok.extern.slf4j.Slf4j;
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
            this.doCommand(message);
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
    public CmdDTO doCommand(final Message message) {
        log.debug("command: {}", message.getText());
        var cmd = Command.of(message);
        return CmdDTO.of(cmd.getExe(), cmd);
    }
}
