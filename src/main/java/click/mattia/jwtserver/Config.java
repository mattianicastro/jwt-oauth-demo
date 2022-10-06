package click.mattia.jwtserver;

import org.json.JSONObject;

public class Config {
    private static JSONObject config = Utils.loadConfig();
    public static String jwtSecret = config.getString("jwtSecret");
    public static String clientId = config.getString("clientId");
    public static String clientSecret = config.getString("clientSecret");
    public static String callbackUrl = config.getString("callbackUrl");
}
