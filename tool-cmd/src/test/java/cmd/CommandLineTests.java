package cmd;

import org.junit.jupiter.api.Test;
import tool.internal.ext.CommandLine;

/**
 * @author max.hu  @date 2024/12/17
 **/
public class CommandLineTests {

    @Test
    public void testCmdParser() {
        var cmds = CommandLine.parseAll("grep -rn -F 'CorbaNotifyTask' ptnlog.log |grep \"msgObjName--\"|awk '{print $8}'|sort|uniq");
        for (var cmd : cmds) {
            System.out.println(cmd);
        }
    }
}
