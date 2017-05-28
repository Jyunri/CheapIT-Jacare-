package br.com.cdf.cheapit;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.location.LocationListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LocationListener {

    BottomBar bottomBar;
    String first_name = "", last_name = "", avatar = "", username = "", facebook_id = "", json;
    String fb_email = "", fb_birthday = "";
    LocationManager locationManager;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get data from Login Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            first_name = extras.getString("first_name");
            last_name = extras.getString("last_name");
            avatar = extras.getString("avatar");
            username = extras.getString("username");
            facebook_id = extras.getString("facebook_id");
            fb_email = extras.getString("email");
            fb_birthday = extras.getString("birthday");
        }

        // Get profile in database by facebook_id
        // TODO: 5/2/17 send email and birthday
        new GetProfile().execute(first_name, last_name, facebook_id, "0");


        // Set Global Current variables
        LoginController.CurrentAvatar = avatar;
        LoginController.CurrentUsername = username;
        LoginController.CurrentEmail = fb_email;


        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: IF NO PERMISSION EXCEPTION
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
        } else {
            LoginController.currentLatitude = 0;
            LoginController.currentLongitude = 0;
        }


        // Welcome toast
        Toast.makeText(this,"Olá, " + first_name + "!",Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Start acitivity with Home Fragment - REVER A NECESSIDADE
        HomeFragment fragment = new HomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction
//                .replace(R.id.fragment_container, fragment)
//                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        //DISABLE DRAWER
        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //toggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Handle Toolbar Logo
        ImageButton ibHomeLogo = (ImageButton)findViewById(R.id.ibHomeLogo);
        ibHomeLogo.setOnClickListener(this); //enable drawer


        // Handle Toolbar Profile Icon
        ImageButton ibProfile = (ImageButton)findViewById(R.id.ibProfile);
        ibProfile.setOnClickListener(this);
        ibProfile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),"Meus Cupons",Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        // Handle Bottom Bar
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case (R.id.tab_home):
                        HomeFragment fragment = new HomeFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction
                                .replace(R.id.fragment_container, fragment)
                                .commit();
                        break;
                    case (R.id.tab_offers):
                        OfferPoolFragment offerPoolFragment = new OfferPoolFragment();
                        android.support.v4.app.FragmentTransaction couponpoolfragmentTransaction = getSupportFragmentManager().beginTransaction();
                        couponpoolfragmentTransaction
                                .replace(R.id.fragment_container, offerPoolFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
//                    case (R.id.tab_fav):
//                        FavoritesFragment favoritesFragment = new FavoritesFragment();
//                        android.support.v4.app.FragmentTransaction favfragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        favfragmentTransaction
//                                .replace(R.id.fragment_container, favoritesFragment)
//                                .commit();
//                        break;
                    case (R.id.tab_partner):
                        PartnerPoolFragment partnerPoolFragment = new PartnerPoolFragment();
                        android.support.v4.app.FragmentTransaction partnerfragmentTransaction = getSupportFragmentManager().beginTransaction();
                        partnerfragmentTransaction
                                .replace(R.id.fragment_container, partnerPoolFragment)
                                .commit();
                        break;
                    case (R.id.tab_map):
                        MapFragment mapFragment = new MapFragment();
                        android.support.v4.app.FragmentTransaction guidefragmentTransaction = getSupportFragmentManager().beginTransaction();
                        guidefragmentTransaction
                                .replace(R.id.fragment_container, mapFragment)
                                .commit();
                        break;
                    case (R.id.tab_logout):
                        LoginManager.getInstance().logOut();
                        goLoginScreen();
                }
            }
        });


        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                switch (tabId) {
                    case (R.id.tab_home):
                        HomeFragment fragment = new HomeFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction
                                .replace(R.id.fragment_container, fragment)
                                .commit();
                        break;
                    case (R.id.tab_offers):
                        OfferPoolFragment offerPoolFragment = new OfferPoolFragment();
                        android.support.v4.app.FragmentTransaction couponpoolfragmentTransaction = getSupportFragmentManager().beginTransaction();
                        couponpoolfragmentTransaction
                                .replace(R.id.fragment_container, offerPoolFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
//                    case (R.id.tab_fav):
//                        FavoritesFragment favoritesFragment = new FavoritesFragment();
//                        android.support.v4.app.FragmentTransaction favfragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        favfragmentTransaction
//                                .replace(R.id.fragment_container, favoritesFragment)
//                                .commit();
//                        break;
                    case (R.id.tab_partner):
                        PartnerPoolFragment partnerPoolFragment = new PartnerPoolFragment();
                        android.support.v4.app.FragmentTransaction partnerfragmentTransaction = getSupportFragmentManager().beginTransaction();
                        partnerfragmentTransaction
                                .replace(R.id.fragment_container, partnerPoolFragment)
                                .commit();
                        break;
                    case (R.id.tab_map):
                        MapFragment mapFragment = new MapFragment();
                        android.support.v4.app.FragmentTransaction guidefragmentTransaction = getSupportFragmentManager().beginTransaction();
                        guidefragmentTransaction
                                .replace(R.id.fragment_container, mapFragment)
                                .commit();
                        break;
                    case (R.id.tab_logout):
                        LoginManager.getInstance().logOut();
                        goLoginScreen();
                }
            }
        });
    }



    // Get profile data from facebook_id
    private class GetProfile extends AsyncTask<String,String,String> {
        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(LoginController.userURL);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("first_name", params[0])
                        .appendQueryParameter("last_name", params[1])
                        .appendQueryParameter("facebook_id", params[2])
                        .appendQueryParameter("age", params[3]);
                String query = builder.build().getEncodedQuery();
                Log.i("User Query",query);

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }

        }


        @Override
        protected void onPostExecute(String result) {
            Log.i("Looking for facebook_id",facebook_id);

            json = result;

            //TODO SHOW TUTORIAL IF NEW USER [UX]
            //No user was found, then flag new User
            // If the json doesnt contain the facebook_id column that implies that its a new user
            if(!json.contains("facebook_id")){
                LoginController.NewUser = true;
                Log.w("User", "New User");
            }else{
                Log.w("User","Old User");
            }

            try {
                JSONObject jsonObj = new JSONObject(json);

                //Getting JSON Array node
                JSONArray user_array = jsonObj.getJSONArray("users_array");
                Log.i("Tamanho do array", String.valueOf(user_array.length()));


                //Expected to iterate one time (one user)
                for (int j = 0; j < user_array.length(); j++) {
                    Log.i("User", "User found");
                    JSONObject c = user_array.getJSONObject(j);
                    LoginController.setCurrentUsername(c.getString("first_name")+" "+c.getString("last_name"));
                    LoginController.CurrentUserId = c.getInt("id");
                }

            } catch (Exception e) {
                Log.e("erro", e.getMessage());
            }
            Log.i("Result", result);
            Log.i("User_id",String.valueOf(LoginController.CurrentUserId));

        }
    }


    // Get Location Methods
    @Override
    public void onLocationChanged(Location location) {
        float lat = (float) (location.getLatitude());
        float lng = (float) (location.getLongitude());
        LoginController.currentLatitude = lat;
        LoginController.currentLongitude = lng;
        Log.i("Lat",String.valueOf(lat));
        Log.i("Lng",String.valueOf(lng));
    }

        // return to login screen
    private void goLoginScreen() {
        Toast.makeText(this,"Indo para tela de login",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,LoginActivity.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            ProfileFragment profileFragment = new ProfileFragment();
            android.support.v4.app.FragmentTransaction profileFragmentTransaction = getSupportFragmentManager().beginTransaction();
            profileFragmentTransaction
                    .replace(R.id.fragment_container, profileFragment)
                    .commit();

        } else if (id == R.id.nav_offers) {
            bottomBar.getTabAtPosition(1).performClick();
            OfferPoolFragment offerPoolFragment = new OfferPoolFragment();
            android.support.v4.app.FragmentTransaction couponpoolfragmentTransaction = getSupportFragmentManager().beginTransaction();
            couponpoolfragmentTransaction
                    .replace(R.id.fragment_container, offerPoolFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_map) {
            bottomBar.getTabAtPosition(3).performClick();
        } else if (id == R.id.nav_points) {
            PointsFragment pointsFragment = new PointsFragment();
            android.support.v4.app.FragmentTransaction pointsFragmentTransaction = getSupportFragmentManager().beginTransaction();
            pointsFragmentTransaction
                    .replace(R.id.fragment_container, pointsFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_help) {
            HelpFragment helpFragment = new HelpFragment();
            android.support.v4.app.FragmentTransaction helpFragmentTransaction = getSupportFragmentManager().beginTransaction();
            helpFragmentTransaction
                    .replace(R.id.fragment_container, helpFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_send) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:audience.cheapit@gmail.com"));

            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                //TODO: Handle case where no email app is available
            }
        }
        else if(id == R.id.nav_logout){
            LoginManager.getInstance().logOut();
            goLoginScreen();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibHomeLogo:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ibProfile:
                ProfileFragment profileFragment = new ProfileFragment();
                android.support.v4.app.FragmentTransaction profileFragmentTransaction = getSupportFragmentManager().beginTransaction();
                profileFragmentTransaction
                        .replace(R.id.fragment_container, profileFragment)
                        .commit();
                break;
            default: break;
        }
    }
}
