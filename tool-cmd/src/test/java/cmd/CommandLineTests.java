package cmd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tool.internal.ext.CommandLine;

/**
 * @author max.hu  @date 2024/12/17
 **/
public class CommandLineTests {

    @Test
    public void testCmdParser() {
        String line = "grep -rn -F \"Corba Notify\" main.log|grep \"msgObj|Name--\"|awk '{print $8}'|sort|uniq";
        var cmds = CommandLine.parse(line);
        var ls1 = cmds.stream().map(e -> e.toString()).reduce((a, b) -> a + CommandLine.CMD_SPLIT + b).get();
        Assertions.assertEquals(line, ls1);
        String line2 = "grep -rn -F 'Corba Notify \"' main.log |grep 'msgObj|Name--\\|'|awk '{print $8}'|sort|uniq";
        var cmds2 = CommandLine.parse(line2);
        var ls2 = cmds2.stream().map(e -> e.toString()).reduce((a, b) -> a + CommandLine.CMD_SPLIT + b).get();
        System.out.println(ls2);

        var l3 = "-x 124 -y 4354 -z -a hello --view";
        var cl3 = CommandLine.parseArgs(l3);
        System.out.println(cl3);
        var l4 = "hello=world a>b x<y";
        var cl4 = CommandLine.parseArgs(l4);
        System.out.println(cl4);
    }
}
