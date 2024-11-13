package tool.internal.cmd;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cmd class管理员
 *
 * @author max.hu  @date 2024/11/04
 **/
public class CmdMethodKeeper {

    // 所有实例化的对象名
    private static final Map<String, Set<String>> classNames = new HashMap<>();

    public static void putClassName(String base, String name) {
        Set<String> set = classNames.computeIfAbsent(base, k -> new HashSet<>());
        set.add(name);
    }

    // 按className找baseId
    public static String getBaseId(String className) {
        for (Map.Entry<String, Set<String>> entry : classNames.entrySet()) {
            for (String cn : entry.getValue()) {
                if (className.equals(cn)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    // 查找是否有扩展类
    public static CmdMethod<?> getExtMethod(String className, String mid) {
        var method = getMethod(className, mid);
        if (null == method || method.getCmdClass().getName().equals(className)) {
            return null;
        }
        return method;
    }

    // 按类名查询Cmd
    public static CmdMethod<?> getMethod(String className, String mid) {
        var baseId = getBaseId(className);
        return null == baseId ? null : get(baseId, mid);
    }

    /**
     * bootClass实例化后的Id -> cmd zoo
     */
    @Getter
    private final static Map<String, CmdClass<?>> classes = new ConcurrentHashMap<>();

    public static CmdMethod<?> get(String base, String mid) {
        var cmdClass = classes.get(base);
        return cmdClass == null ? null : cmdClass.getMethod(mid);
    }

    public static void put(String base, CmdClass<?> cmdClass) {
        classes.put(base, cmdClass);
    }
}
