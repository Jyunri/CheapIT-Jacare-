package br.com.cdf.cheapit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button guestLogin;
    private LoginButton fbLogin;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                nextActivity(currentProfile);
            }
        };


        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        fbLogin = (LoginButton) findViewById(R.id.lbFbLogin);
        guestLogin = (Button) findViewById(R.id.ibGuestLogin);

        //Facebook Login
        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                if(profile == null){
                    accessTokenTracker.startTracking();
                    profileTracker.startTracking();
                }
                else {
                    nextActivity(profile);
                }
                Toast.makeText(getApplicationContext(), "Entrando..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };
        fbLogin.registerCallback(callbackManager, callback);

        // Guest Login
        guestLogin.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }

    private void nextActivity(Profile profile){
        if(profile != null){
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            main.putExtra("first_name", profile.getFirstName());
            startActivity(main);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.ibGuestLogin):
                Toast.makeText(this,"login visitante",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("key","guest");
                i.putExtra("first_name","Visitante");
                startActivity(i);
                break;
        }
    }

        /* Testando novo metodo
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

    */



}
