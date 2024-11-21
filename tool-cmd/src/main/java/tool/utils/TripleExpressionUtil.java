package tool.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author max.hu  @date 2024/11/20
 **/
public class TripleExpressionUtil {

    // &&
    public static boolean and(boolean bool, Triple<String, String, String> tx, Object... ctx) {
        return bool && ExpressionParser.bool(tx.left + tx.middle + tx.right, ctx);
    }

    // && 一组表达式
    public static boolean and(List<Triple<String, String, String>> txs, Object... args) {
        boolean bool = true;
        for (var tx : txs) {
            bool = and(bool, tx, args);
        }
        return bool;
    }

    private static final String regex = "(.*?)([<>!=]=?|=)(.*)";    // 按比较运算符分隔
    private static final Pattern pattern = Pattern.compile(regex);

    public static Triple<String, String, String> extractRelation(String relationString) {
        Matcher matcher = pattern.matcher(relationString);
        if (matcher.find()) {
            String subject = matcher.group(1).trim();
            String predicate = matcher.group(2).trim();
            String object = matcher.group(3).trim();
            return new Triple<>(subject, predicate, object);
        } else {
            throw new RuntimeException("relation string: " + relationString);
        }
    }

    /**
     * 变量变换
     *
     * @param relations 一组关系表达式
     * @param keyFields 映射到对象的属性
     * @param obj       obj id
     * @return
     */
    public static List<Triple<String, String, String>> explain(String[] relations, Map<String, String> keyFields, String obj) {
        List<Triple<String, String, String>> triples = new LinkedList<>();
        for (String s : relations) {
            var triple = explain(s, keyFields, obj);
            if (null != triple) triples.add(triple);
        }
        return triples;
    }

    public static List<Triple<String, String, String>> explain(String[] relations, Map<String, String> keyFields) {
        return explain(relations, keyFields, null);
    }

    public static Triple<String, String, String> explain(String relation, Map<String, String> fields) {
        return explain(relation, fields, null);
    }

    public static Triple<String, String, String> explain(String relation, Map<String, String> fields, String obj) {
        var triple = extractRelation(relation);
        if (null == triple) return null;
        var lf = fields.get(triple.left);
        if (null != lf) {
            if (null == obj) {
                triple.setLeft(lf);
            } else {
                triple.setLeft(obj + "." + lf);
            }
        }
        if (triple.middle.equals("=")) {
            triple.setMiddle("==");
        }
        var rf = fields.get(triple.right);
        if (null != rf) {
            if (null == obj) {
                triple.setRight(rf);
            } else {
                triple.setRight(obj + "." + rf);
            }
        }
        return triple;
    }

    // 解析数字符 - K、M、B
    private static final Set<String> numUnits = Set.of("K", "k", "M", "m", "B", "b");

    // 简单处理右侧的数字
    public static void doRNumUnit(final List<Triple<String, String, String>> triples) {
        triples.forEach(e -> doRNumUnit(e));
    }

    public static void doRNumUnit(final Triple<String, String, String> triple) {
        var num = triple.right.substring(0, triple.right.length() - 1);
        var sign = triple.right.substring(triple.right.length() - 1);
        if (PrimitiveValueUtil.isNumeric(num) && numUnits.contains(sign)) {
            if (sign.equalsIgnoreCase("K")) {
                triple.setRight(PrimitiveValueUtil.stringValue(PrimitiveValueUtil.doubleValue(num) * 1000));
            } else if (sign.equalsIgnoreCase("M")) {
                triple.setRight(PrimitiveValueUtil.stringValue(PrimitiveValueUtil.doubleValue(num) * 1000000));
            } else if (sign.equalsIgnoreCase("B")) {
                triple.setRight(PrimitiveValueUtil.stringValue(PrimitiveValueUtil.doubleValue(num) * 1000000000));
            }
        }
    }
}
