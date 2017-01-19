package br.com.cdf.cheapit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
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

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    BottomBar bottomBar;
    String loginType = "", first_name = "", avatar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loginType = extras.getString("key");
            first_name = extras.getString("first_name");
            avatar = extras.getString("avatar");
        }

        FacebookController.setCurrentAvatar(avatar);
        /*Toast.makeText(this,"Olá, " + FacebookController.getCurrentFirstName(),Toast.LENGTH_LONG).show();*/
        Toast.makeText(this,"Olá, " + first_name,Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Remove o menu de 3 bolinhas a direita do toolbar
        //setSupportActionBar(toolbar);

        HomeFragment fragment = new HomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.fragment_container, fragment)
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton ibHomeLogo = (ImageButton)findViewById(R.id.ibHomeLogo);
        ibHomeLogo.setOnClickListener(this);
        ibHomeLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),"Meus Cupons",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ImageButton ibMyCoupons = (ImageButton)findViewById(R.id.ibMyCoupons);
        ibMyCoupons.setOnClickListener(this);


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
                    case (R.id.tab_coupons):
                        CouponFragment couponFragment = new CouponFragment();
                        android.support.v4.app.FragmentTransaction couponfragmentTransaction = getSupportFragmentManager().beginTransaction();
                        couponfragmentTransaction
                                .replace(R.id.fragment_container, couponFragment)
                                .commit();
                        break;
                    case (R.id.tab_fav):
                        FavoritesFragment favoritesFragment = new FavoritesFragment();
                        android.support.v4.app.FragmentTransaction favfragmentTransaction = getSupportFragmentManager().beginTransaction();
                        favfragmentTransaction
                                .replace(R.id.fragment_container, favoritesFragment)
                                .commit();
                        break;
                    case (R.id.tab_map):
                        GuideFragment guideFragment = new GuideFragment();
                        android.support.v4.app.FragmentTransaction guidefragmentTransaction = getSupportFragmentManager().beginTransaction();
                        guidefragmentTransaction
                                .replace(R.id.fragment_container, guideFragment)
                                .commit();
                        break;
                    case (R.id.tab_logout):
                        LoginManager.getInstance().logOut();
                        // Writing data to SharedPreferences
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("SaveFiles", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("login", "");
                        editor.commit();
                        Log.d("Logout cached AT","LogoutAT: "+preferences.getString("login",""));
                        goLoginScreen();
                }
            }
        });

        // Handles if coupon tab pressed on MyCoupon fragment
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
                    case (R.id.tab_coupons):
                        CouponFragment couponFragment = new CouponFragment();
                        android.support.v4.app.FragmentTransaction couponfragmentTransaction = getSupportFragmentManager().beginTransaction();
                        couponfragmentTransaction
                                .replace(R.id.fragment_container, couponFragment)
                                .commit();
                        break;
                }
            }
        });
    }

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
            android.support.v4.app.FragmentTransaction couponfragmentTransaction = getSupportFragmentManager().beginTransaction();
            couponfragmentTransaction
                    .replace(R.id.fragment_container, profileFragment)
                    .commit();

        } else if (id == R.id.nav_coupons) {
            bottomBar.getTabAtPosition(1).performClick();
        } else if (id == R.id.nav_map) {
            bottomBar.getTabAtPosition(3).performClick();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

//                HomeFragment fragment = new HomeFragment();
//                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction
//                        .replace(R.id.fragment_container, fragment)
//                        .commit();
                break;
            case R.id.ibMyCoupons:
                bottomBar.selectTabAtPosition(1);    //simula o clique sem chamar as acoes
                MyCouponsFragment myCouponsFragment = new MyCouponsFragment();
                android.support.v4.app.FragmentTransaction couponfragmentTransaction = getSupportFragmentManager().beginTransaction();
                couponfragmentTransaction
                        .replace(R.id.fragment_container, myCouponsFragment)
                        .commit();
                break;
            default: break;
        }
    }
}
