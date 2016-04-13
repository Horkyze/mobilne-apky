package sk.stuba.fiit.revizori;

import android.app.Application;
import android.content.Context;

public class Revizori extends Application {

        private static Context context;

        public void onCreate() {
            super.onCreate();
            Revizori.context = getApplicationContext();
        }

        public static Context getAppContext() {
            return Revizori.context;
        }
}

