package tool.internal.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * CMD数据传递
 * 参数和命令 - 封装参数和执行的命令
 *
 * @author max.hu  @date 2024/11/06
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CmdDTO {
    // id
    private String id;
    // to expression
    private String to;
    // args - input parameters
    private Object[] args;

    public CmdDTO putArgs(Object... objects) {
        this.args = objects;
        return this;
    }

    // 跳转uri
    public String to() {
        if (null == this.getTo()) {
            return "";
        }
        return this.getTo().startsWith("/") ? this.getTo() : "/" + this.getTo();
    }

    public static CmdDTO of(String to, Object... args) {
        return CmdDTO.builder().to(to).args(args).build();
    }
}
