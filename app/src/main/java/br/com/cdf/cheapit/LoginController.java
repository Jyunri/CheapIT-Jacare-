package br.com.cdf.cheapit;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Jimy on 1/4/17.
 */

public class LoginController extends MultiDexApplication {
//    public static final String offerURL = "https://cheapit.000webhostapp.com/page_offers_json_jimy.php";
//    public static final String partnerURL = "https://cheapit.000webhostapp.com/page_partners_json_jimy.php";
//    public static final String userURL = "https://cheapit.000webhostapp.com/page_users_json.php";
//    public static final String new_couponURL = "https://cheapit.000webhostapp.com/page_json_new_coupon_beta.php";
//    public static final String my_couponURL = "https://cheapit.000webhostapp.com/page_coupons_ordered_json.php";
//    public static final String update_coupon_URL = "https://cheapit.000webhostapp.com/update_coupon.php";

    public static final String offerURL = "http://cheapit1.tempsite.ws/query/page_offers_json_jimy.php";
    public static final String partnerURL = "http://cheapit1.tempsite.ws/query/page_partners_json_jimy.php";
    public static final String userURL = "http://cheapit1.tempsite.ws/query/page_users_json.php";
    public static final String new_couponURL = "http://cheapit1.tempsite.ws/query/page_json_new_coupon_beta.php";
    public static final String my_couponURL = "http://cheapit1.tempsite.ws/query/page_coupons_ordered_json.php";
    public static final String update_coupon_URL = "http://cheapit1.tempsite.ws/query/update_coupon.php";



    public static String CurrentFirstName = "Visitante";
    public static String CurrentUsername = "Visitante";
    public static String CurrentAvatar;
    public static String LoginMethod;

    public static int CurrentUserId = 0;
    public static boolean NewUser = false;
    public static String CurrentEmail;
    public static float currentLatitude;
    public static float currentLongitude;

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
