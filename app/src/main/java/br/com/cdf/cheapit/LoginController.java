package br.com.cdf.cheapit;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Jimy on 1/4/17.
 */

public class LoginController extends MultiDexApplication {
    public static final String offerURL = "https://cheapit.000webhostapp.com/page_offers_json_jimy.php";
    public static final String partnerURL = "https://cheapit.000webhostapp.com/page_partners_json_jimy.php";

    public static String CurrentFirstName = "Visitante";
    public static String CurrentUsername = "Visitante";
    public static String CurrentAvatar;
    public static String LoginMethod;

    public static String CurrentUserId = "2";

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

    public static String getCurrentUsername() {
        return LoginController.CurrentUsername;
    }

    public static void setCurrentUsername(String currentUserName) {
        LoginController.CurrentUsername = currentUserName;
    }
}
