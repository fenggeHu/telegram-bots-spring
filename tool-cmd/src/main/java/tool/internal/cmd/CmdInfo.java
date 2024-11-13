package tool.internal.cmd;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Cmd注解信息
 * 参数和命令 - 封装参数和执行的命令
 *
 * @author max.hu  @date 2024/11/06
 **/
@Data
@SuperBuilder
public class CmdInfo {
    // id
    private String id;
    // to expression
    private String to;
    // cmd class
    private Class<?> clazz;
}
