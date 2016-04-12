package sk.stuba.fiit.revizori.imgur;

import android.util.Log;

public class aLog {
    private static final boolean LOGGING = false;
    public static void w (String TAG, String msg){
        if(LOGGING) {
            if (TAG != null && msg != null)
                Log.w(TAG, msg);
        }
    }
}
