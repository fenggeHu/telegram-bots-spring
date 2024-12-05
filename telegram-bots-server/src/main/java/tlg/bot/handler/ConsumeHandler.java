package tlg.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 消费消息 - consume(Update update)
 *
 * @author max.hu  @date 2024/12/04
 **/
public interface ConsumeHandler {
    // 判断是否匹配: true - 匹配； false - 不匹配
    boolean matched(final Update update);

    // 当 matched = true时执行本方法
    // 返回值：是否继续下一个handler。 true - 执行； false - 不执行
    boolean execute(final Update update, final Object owner);
}
