package cmd;

import org.junit.jupiter.api.Test;
import tool.internal.ext.CommandLine;

/**
 * @author max.hu  @date 2024/12/17
 **/
public class CommandLineTests {

    @Test
    public void testCmdParser() {
        String line = "grep -rn -F \"Corba Notify\" main.log |grep \"msgObj|Name--\"|awk '{print $8}'|sort|uniq";
        var cmds = CommandLine.parseAll(line);

        String line2 = "grep -rn -F 'Corba Notify \"' main.log |grep 'msgObj|Name--\\|'|awk '{print $8}'|sort|uniq";
        var cmds2 = CommandLine.parseAll(line2);
        System.out.println(cmds2);
    }
}
