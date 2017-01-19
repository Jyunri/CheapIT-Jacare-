package br.com.cdf.cheapit;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Jimy on 1/4/17.
 */

public class FacebookController extends MultiDexApplication {
    public static String CurrentFirstName = "Visitante";
    public static String CurrentAvatar;
    public static String LoginMethod;


    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public static void setLoginMethod(String loginMethod) {
        LoginMethod = loginMethod;
    }

    public static String getCurrentFirstName() {
        return CurrentFirstName;
    }

    public static void setCurrentFirstName(String currentFirstName) {
        CurrentFirstName = currentFirstName;
    }

    public static String getCurrentAvatar() {
        return CurrentAvatar;
    }

    public static void setCurrentAvatar(String currentAvatar) {
        CurrentAvatar = currentAvatar;
    }
}
