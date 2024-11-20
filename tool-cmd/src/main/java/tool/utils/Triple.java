package tool.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author jinfeng.hu  @date 2022/10/8
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Triple<L, M, R> {
    @Setter
    public L left;
    @Setter
    public M middle;
    @Setter
    public R right;

    public static <L, M, R> Triple<L, M, R> of(L left, M middle, R right) {
        return new Triple(left, middle, right);
    }
}
