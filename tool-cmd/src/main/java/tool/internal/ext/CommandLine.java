package tool.internal.ext;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 把命令行解析成命令-参数形式
 *
 * @author max.hu  @date 2024/12/17
 **/
public class CommandLine {
    private static final String CMD_SPLIT = "\\|";
    private static final String SPACE = " ";
    String command; // 命令
    Map<String, List<String>> options;  // 选项
    List<String> arguments; // 参数

    public CommandLine(String command, Map<String, List<String>> options, List<String> arguments) {
        this.command = command;
        this.options = options;
        this.arguments = arguments;
    }

    // 解析命令行 - 命令行有顺序
    // eg: grep -rn -F "Corba" main.log |grep "ObjName==M"|awk '{print $8}'|sort|uniq
    public static List<CommandLine> parseAll(String line) {
        String[] cmds = line.split(CMD_SPLIT);
        List<CommandLine> ret = new LinkedList<>();
        for (String cmd : cmds) {
            String s = cmd.trim();
            CommandLine clr = parse(s);
            ret.add(clr);
        }
        return ret;
    }

    // 使用正则表达式拆分命令行，保留引号内的内容作为一个整体
    private static Pattern pattern = Pattern.compile("'([^']*)'|\"([^\"]*)\"|([^\\s]+)");

    // 解析一个命令
    // eg: grep -rn -F "Corba" main.log
    public static CommandLine parse(String cmdString) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = pattern.matcher(cmdString);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // 处理单引号内的内容
                tokens.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // 处理双引号内的内容
                tokens.add(matcher.group(2));
            } else {
                // 处理没有引号的内容
                tokens.add(matcher.group(3));
            }
        }

        String command = tokens.get(0);  // 第一个部分是命令本身
        Map<String, List<String>> options = new LinkedHashMap<>();
        List<String> arguments = new ArrayList<>();

        String currentOption = null;  // 当前处理的选项
        for (String token : tokens) {
            // 处理选项（以 "-" 开头的部分）
            if (token.startsWith("-")) {
                // 如果是复合选项（如 -rn），保持其原样作为一个选项
                currentOption = token;
                options.putIfAbsent(currentOption, new ArrayList<>());
            } else {
                // 如果当前选项有值，则与该选项关联
                if (currentOption != null) {
                    options.get(currentOption).add(token);
                    currentOption = null;  // 处理完当前选项，清空当前选项
                } else {
                    // 无选项的参数（如文件名）
                    arguments.add(token);
                }
            }
        }

        return new CommandLine(command, options, arguments);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(command).append(SPACE);
        if (null != options) {
            options.forEach((k, v) -> {
                sb.append(k);
                if (null != v && !v.isEmpty()) {
                    v.forEach(ve -> sb.append(SPACE).append(ve));
                }
                sb.append(SPACE);
            });
        }
        if (null != arguments) {
            arguments.forEach(v -> sb.append(v).append(SPACE));
        }
        return sb.toString();
    }

}