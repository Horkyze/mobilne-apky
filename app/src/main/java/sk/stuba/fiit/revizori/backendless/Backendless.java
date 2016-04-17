package sk.stuba.fiit.revizori.backendless;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hork on 17/04/16.
 */
public class Backendless {

    public static String url = "http://api.backendless.com/v1/data/revizor";
    public static String appId = "FAE4BDAB-E4A4-654C-FFFA-A005AD5E5D00";
    public static String secret = "B2C47E23-06D1-5232-FF77-07E29B78AA00";

    public static Map<String, String> getHeaders(){
        Map map = new HashMap<>();
        map.put("application-id", Backendless.appId);
        map.put("secret-key", Backendless.secret);
        return map;
    }

}
