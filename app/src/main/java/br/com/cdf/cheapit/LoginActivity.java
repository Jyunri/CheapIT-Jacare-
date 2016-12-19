package br.com.cdf.cheapit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton fbLogin, googleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fbLogin = (ImageButton)findViewById(R.id.ibFbLogin);
        googleLogin =  (ImageButton)findViewById(R.id.ibGoogleLogin);

        fbLogin.setOnClickListener(this);
        googleLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.ibFbLogin):
                Toast.makeText(this,"login via facebook",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,MainActivity.class));
                break;
            case (R.id.ibGoogleLogin):
                Toast.makeText(this,"login via google",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
    }
}
