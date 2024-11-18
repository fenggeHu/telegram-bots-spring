package tool.internal.cmd;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * cmd class管理员
 *
 * @author max.hu  @date 2024/11/04
 **/
@Slf4j
public class CmdMethodKeeper {

    // 所有实例化的对象
    private static final Map<String, Set<Object>> instances = new HashMap<>();

    public static void addInstance(String base, Object instance) {
        Set<Object> set = instances.computeIfAbsent(base, k -> new HashSet<>());
        set.add(instance);
    }

    // 按className找baseId
    public static String getBaseId(Object ths) {
        for (Map.Entry<String, Set<Object>> entry : instances.entrySet()) {
            for (Object cn : entry.getValue()) {
                if (ths.equals(cn)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    // 按实例和查询Cmd
    public static CmdMethod<?> getMethod(Object ths, String cmdId) {
        var baseId = getBaseId(ths);
        return null == baseId ? null : get(baseId, cmdId);
    }

    /**
     * bootClass实例化后的Id -> cmd class
     */
    @Getter
    private final static Map<String, CmdClass<?>> bootClasses = new HashMap<>();

    private static CmdMethod<?> get(String base, String to) {
        var cmdClass = bootClasses.get(base);
        return cmdClass == null ? null : cmdClass.getMethod(to);
    }

    public static void set(String base, CmdClass<?> cmdClass) {
        bootClasses.put(base, cmdClass);
    }

    // 输出日志
    public static String log() {
        var sb = new StringBuilder();
        bootClasses.forEach((k, v) -> {
            sb.append("Boot: ").append(k).append("===>\n");
            v.getMethods().forEach((p, m) -> {
                sb.append("\t").append(p).append(" -> ").append(m.getClazz().getName())
                        .append(":").append(m.getMethod().getName()).append("\n");
            });
        });
        log.info(sb.toString());
        return sb.toString();
    }
}
