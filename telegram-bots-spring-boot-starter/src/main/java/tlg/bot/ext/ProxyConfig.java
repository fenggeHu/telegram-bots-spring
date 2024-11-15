package tlg.bot.ext;

import lombok.Data;

/**
 * @author max.hu  @date 2024/11/15
 **/
@Data
public class ProxyConfig {
    private String host;
    private int port;
    private String type; // SOCKS\HTTP\DIRECT
}
