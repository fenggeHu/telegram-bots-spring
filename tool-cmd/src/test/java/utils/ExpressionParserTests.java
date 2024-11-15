package utils;

import org.junit.jupiter.api.Test;
import tool.utils.ExpressionParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author max.hu  @date 2024/11/15
 **/
public class ExpressionParserTests {

    @Test
    public void testStrX() {
        var arr = new String[]{"a1", "a2", "a3"};
        var list = new ArrayList<>(List.of("x5", "e1", "e2"));
        var map = Map.of("k1", "v1", "k2", "v2");

        var s = ExpressionParser.strX("排序: ${Collections.sort(x1);x1}\n输出数组: ${Arrays.toString(x0)}", arr, list, map);
        System.out.println(s);
    }
}
