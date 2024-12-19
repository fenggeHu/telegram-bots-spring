package utils;

import dto.User;
import org.junit.jupiter.api.Test;
import tool.utils.ExpressionParser;
import tool.utils.PrimitiveValueUtil;
import tool.utils.TripleExpressionUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author max.hu  @date 2024/11/15
 **/
public class ExpressionParserTests {
    static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?"); // 整数或小数

    @Test
    public void testNumber() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            var b = isNumeric("-0.0011001012");
            b = isNumeric("-11.0010123");
            b = isNumeric("-11001012.3");
            b = isNumeric("110010123");
            b = isNumeric("adf12");
            b = isNumeric("1243.2df");
            b = isNumeric("1.1001012E3");
        }
        System.out.println(System.currentTimeMillis() - start);

        long l = 1234567890123456L;
        System.out.println(l);
        System.out.println(PrimitiveValueUtil.stringValue(l));
        double d = 10000000000.123;
        System.out.println(PrimitiveValueUtil.stringValue(d));
        System.out.println(BigDecimal.valueOf(d).toPlainString());
    }

    public static boolean isNumeric(String str) {
        return pattern.matcher(str).matches();
    }

    @Test
    public void testRegexParser() {
        var t1 = TripleExpressionUtil.extractRelation("A>2.5B");
        var t2 = TripleExpressionUtil.extractRelation("A>=2.5B");
        var t4 = TripleExpressionUtil.extractRelation("A<2.5");
        var t5 = TripleExpressionUtil.extractRelation("A<=2500");
        var t3 = TripleExpressionUtil.extractRelation("A=2.5B");
        var t6 = TripleExpressionUtil.extractRelation("3A<B+c");
        var t7 = TripleExpressionUtil.extractRelation("2.5A+B<c");
        var t72 = TripleExpressionUtil.extractRelation("A+B<c");
        var t8 = TripleExpressionUtil.extractRelation("A=B+c");
        var t9 = TripleExpressionUtil.extractRelation("A=B*c");
        var t10 = TripleExpressionUtil.extractRelation("A==B*c");
        var t11 = TripleExpressionUtil.extractRelation("A!=B*c");
        System.out.println();
    }

    @Test
    public void testCollectionParser() {
        var arr = new String[]{"a1", "a2", "a3"};
        var list = new ArrayList<>(List.of("x5", "e1", "e2"));
        var map = Map.of("k1", "v1", "k2", "v2");

        var s = ExpressionParser.str("排序: ${Collections.sort(x1);x1}\n输出数组: ${Arrays.toString(x0)}", arr, list, map);
        System.out.println(s);

        var s2 = ExpressionParser.str("${String.join(\",\",x0)}", list);
        System.out.println();
    }

    @Test
    public void testArgs0() {
        User u = User.builder().name("刘备").code("u1").build();
//        u.setInfo(Map.of("age", 18));
        var es = ExpressionParser.str("${name}-${code}: ${null==info?'':'Age='+info.get('age')}", u);
        System.out.println(es);
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
//        CmdInfo ci1 = CmdInfo.builder().id("hello").to("world").notes("teeee").build();
//        CmdInfo ci2 = CmdInfo.builder().id("id2").to("world").notes("i'm 2").build();
//        vars.put("ci1", ci1);
//        vars.put("ci2", ci2);
        var r1 = ExpressionParser.execute("${ci1.to} == ${ci2.to}", vars);
        System.out.println(r1);
        var r2 = ExpressionParser.execute("${ci1.to} == ${ci2.notes}", vars);
        System.out.println(r2);
        var r4 = ExpressionParser.execute("${x0}", 123);
        System.out.println(r4);

        var v1 = ExpressionParser.eval("ci1.to == ci2.to", vars);
        System.out.println(v1);
        var v2 = ExpressionParser.eval("ci1.to == ci2.notes", vars);
        System.out.println(v2);
        var v4 = ExpressionParser.eval("x0", 123);
        System.out.println(v4);
    }

}
