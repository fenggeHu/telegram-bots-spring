package dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * @author max.hu  @date 2024/12/07
 **/
@Data
@SuperBuilder
public class User {
    private Long id;
    private String code;
    private String name;
    private Map<String, Object> info;
}
