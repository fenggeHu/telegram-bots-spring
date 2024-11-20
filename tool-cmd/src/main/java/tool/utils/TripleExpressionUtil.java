package tool.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author max.hu  @date 2024/11/20
 **/
public class TripleExpressionUtil {

    // &&
    public static boolean and(boolean bool, Triple<String, String, String> tx, Object... args) {
        return bool && ExpressionParser.boolX(tx.left + tx.middle + tx.right, args);
    }
    // && 一组表达式
    public static boolean and(List<Triple<String, String, String>> txs, Object... args) {
        boolean bool = true;
        for (var tx : txs) {
            bool = and(bool, tx, args);
        }
        return bool;
    }

    private static final String regex = "([A-Za-z0-9]+)\\s*(<=|>=|<|>|=)\\s*([A-Za-z0-9]+|[0-9]+\\.?[0-9]*)$";
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

    public static Triple<String, String, String> explain(String relation, Map<String, String> keyFields, String obj) {
        var triple = extractRelation(relation);
        if (null == triple) return null;
        var lf = keyFields.get(triple.left);
        if (null != lf) {
            if (null == obj) {
                triple.setLeft("${" + lf + "}");
            } else {
                triple.setLeft("${" + obj + "." + lf + "}");
            }
        }
        var rf = keyFields.get(triple.right);
        if (null != rf) {
            if (null == obj) {
                triple.setRight("${" + rf + "}");
            } else {
                triple.setRight("${" + obj + "." + rf + "}");
            }
        }
        return triple;
    }

}
