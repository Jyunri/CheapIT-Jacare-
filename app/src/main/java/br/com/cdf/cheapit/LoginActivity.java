package br.com.cdf.cheapit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton googleLogin;
    private LoginButton fbLogin;
    private CallbackManager callbackManager;
    String firstName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        fbLogin = (LoginButton) findViewById(R.id.lbFbLogin);
        googleLogin =  (ImageButton)findViewById(R.id.ibGoogleLogin);

        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                if(Profile.getCurrentProfile()== null){
                    Log.w("Profile changed","Tracking new profile");
                    ProfileTracker profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            stopTracking();
                            Log.d("facebook - newprofile", currentProfile.getFirstName());
                            Profile.setCurrentProfile(currentProfile);
                            FacebookController.setCurrentFirstName(currentProfile.getFirstName());
                        }
                    };
                    profileTracker.startTracking();
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.d("facebook - profile", profile.getFirstName());
                    FacebookController.setCurrentFirstName(profile.getFirstName());
                }


                SharedPreferences preferences = getApplicationContext().getSharedPreferences("SaveFiles", 0); // 0 - for private mode
                Log.d("Cached before login","BeforeLoginAT: " + preferences.getString("login",""));

                // Writing data to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("login", AccessToken.getCurrentAccessToken().getToken());
                editor.commit();

                Log.d("Cached after login","AfterLoginAT: " + preferences.getString("login",""));

                Toast.makeText(getApplicationContext(),"login via facebook",Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"login canceled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"login error",Toast.LENGTH_SHORT).show();
            }
        });

        googleLogin.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.ibGoogleLogin):
                Toast.makeText(this,"login visitante",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("key","guest");
                i.putExtra("firstName","Visitante");
                startActivity(i);
                break;
        }
    }
}
