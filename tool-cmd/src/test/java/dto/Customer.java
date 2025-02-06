package dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * @author max.hu  @date 2025/02/06
 **/
@Data
@SuperBuilder
public class Customer {
    private String id;
    private String code;
    private String name;
    private Map<String, Object> info;
}
