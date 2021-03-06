package br.com.cdf.cheapit;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO remove guestLogin [PD]
    Button guestLogin;

    private LoginButton fbLogin;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    public String fb_birthday = "";
    public String fb_email = "";

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

        fbLogin.setReadPermissions(Arrays.asList("email","user_birthday"));


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

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    Log.v("LoginActivity Response ", response.toString());

                                    try {
                                        fb_birthday = object.getString("user_birthday");
                                        fb_email = object.getString("email");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();

                    nextActivity(profile);
                }
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
            LoginController.setLoginMethod("facebook");
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            main.putExtra("first_name", profile.getFirstName());
            main.putExtra("last_name", profile.getLastName());
            // TODO: 4/30/17 get email
            main.putExtra("email",fb_email);
            main.putExtra("birthday",fb_birthday);
            main.putExtra("avatar", profile.getProfilePictureUri(200,200).toString());
            main.putExtra("username",profile.getName());
            main.putExtra("facebook_id",profile.getId());
            Log.i("Facebook_id",profile.getId());
            Toast.makeText(getApplicationContext(), "Carregando..", Toast.LENGTH_SHORT).show();
            startActivity(main);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.ibGuestLogin):
                LoginController.setLoginMethod("guest");
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("first_name","Visitante");
                i.putExtra("username","Visitante");
                Toast.makeText(this,"Carregando..",Toast.LENGTH_SHORT).show();
                startActivity(i);
                break;
        }
    }



}
