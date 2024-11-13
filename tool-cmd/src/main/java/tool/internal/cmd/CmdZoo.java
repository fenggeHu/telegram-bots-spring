package tool.internal.cmd;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 主类和cmd信息
 *
 * @author max.hu  @date 2024/11/04
 **/
public class CmdZoo {
    // boot bean id/name
    @Setter
    @Getter
    protected String id;
    // 入口类/主类信息
    @Getter
    protected Class<?> bootClass;
    // 所有含注解的method信息 - bean name/value -> Method
    protected final Map<String, CmdMethod> methods = new HashMap<>();

    public CmdZoo(String bootId, Class<?> bootClass) {
        this.id = bootId;
        this.bootClass = bootClass;
    }

    public CmdMethod get(String id) {
        return this.methods.get(id);
    }

    public CmdZoo put(String id, CmdMethod cmi) {
        this.methods.put(id, cmi);
        return this;
    }

    // 扫描Router注解
    public CmdZoo scan() {

        return this;
    }
}
