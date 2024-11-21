package utils;

import org.junit.jupiter.api.Test;
import tool.internal.cmd.CmdInfo;
import tool.utils.ExpressionParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author max.hu  @date 2024/11/15
 **/
public class ExpressionParserTests {

    @Test
    public void testCollectionParser() {
        var arr = new String[]{"a1", "a2", "a3"};
        var list = new ArrayList<>(List.of("x5", "e1", "e2"));
        var map = Map.of("k1", "v1", "k2", "v2");

        var s = ExpressionParser.str("排序: ${Collections.sort(x1);x1}\n输出数组: ${Arrays.toString(x0)}", arr, list, map);
        System.out.println(s);
    }

    @Test
    public void testConstants() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            var arr = new String[]{"a1", "a2", "a3"};
            var list = new ArrayList<>(List.of("x5", "e1", "e2"));
            var map = Map.of("k1", "v1", "k2", "v2");
            var s = ExpressionParser.str("/hello/go", arr, list, map);
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void testParser() {
        Map<String, Object> vars = new HashMap<>();
        CmdInfo ci1 = CmdInfo.builder().id("hello").to("world").notes("teeee").build();
        CmdInfo ci2 = CmdInfo.builder().id("id2").to("world").notes("i'm 2").build();
        vars.put("ci1", ci1);
        vars.put("ci2", ci2);
        var r1 = ExpressionParser.execute("${ci1.to} == ${ci2.to}", vars);
        System.out.println(r1);
        var r2 = ExpressionParser.execute("${ci1.to} == ${ci2.notes}", vars);
        System.out.println(r2);
        var r3 = ExpressionParser.execute("${x0.to} == ${notes}", ci1);
        System.out.println(r3);
        var r4 = ExpressionParser.execute("${x0}", 123);
        System.out.println(r4);

        var v1 = ExpressionParser.eval("ci1.to == ci2.to", vars);
        System.out.println(v1);
        var v2 = ExpressionParser.eval("ci1.to == ci2.notes", vars);
        System.out.println(v2);
        var v3 = ExpressionParser.eval("x0.to == notes", ci1);
        System.out.println(v3);
        var v4 = ExpressionParser.eval("x0", 123);
        System.out.println(v4);
    }

}
