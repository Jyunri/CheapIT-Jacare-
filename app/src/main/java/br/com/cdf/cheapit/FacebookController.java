package br.com.cdf.cheapit;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Jimy on 1/4/17.
 */

public class FacebookController extends MultiDexApplication {
    public static String CurrentFirstName = "Visitante";

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public static String getCurrentFirstName() {
        return CurrentFirstName;
    }

    public static void setCurrentFirstName(String currentFirstName) {
        CurrentFirstName = currentFirstName;
    }
}
