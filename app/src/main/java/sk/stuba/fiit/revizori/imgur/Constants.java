package sk.stuba.fiit.revizori.imgur;

public class Constants {
    /*
      Logging flag
     */
    public static final boolean LOGGING = false;

    /*
      Your imgur client id. You need this to upload to imgur.
      More here: https://api.imgur.com/
     */
    public static final String MY_IMGUR_CLIENT_ID = "2fb884029012645";
    public static final String MY_IMGUR_CLIENT_SECRET = "0ccc6b483f1e60e218d0251b9af35c17abc1b859";

    /*
      Redirect URL for android.
     */
    public static final String MY_IMGUR_REDIRECT_URL = "";

    public static final String ALBUM_ID = "revizori";

    /*
      Client Auth
     */
    public static String getClientAuth() {
        return "Client-ID " + MY_IMGUR_CLIENT_ID;
    }

}
