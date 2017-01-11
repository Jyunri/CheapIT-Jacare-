package br.com.cdf.cheapit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.Profile;

/**
 * Created by Jimy on 12/17/16.
 */

public class Splash extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("SaveFiles", 0); // 0 - for private mode
        final String cachedLogin = preferences.getString("login","");


        new Handler().postDelayed(new Runnable() {

         /*
          * Showing splash screen with a timer. This will be useful when you
          * want to show case your app logo / company
          */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if(cachedLogin.isEmpty())
                {
                    Intent i = new Intent(Splash.this, LoginActivity.class);
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    Log.d("AccessToken","AccessToken: "+ AccessToken.getCurrentAccessToken().getToken());
                    Log.d("Cached AccessToken","Cached AccessToken: "+ cachedLogin);
                    //Profile.fetchProfileForCurrentAccessToken();
                    Profile profile = Profile.getCurrentProfile();
                    FacebookController.setCurrentFirstName(profile.getFirstName());
                    i.putExtra("AccessToken",cachedLogin);
                    startActivity(i);
                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
