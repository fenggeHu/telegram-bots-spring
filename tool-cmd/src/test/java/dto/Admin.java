package dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author max.hu  @date 2024/12/07
 **/
@Data
@SuperBuilder
public class Admin extends User {
    private String department;
    private List<String> roles;
}
