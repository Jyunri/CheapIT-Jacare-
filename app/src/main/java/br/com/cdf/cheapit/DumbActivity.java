package br.com.cdf.cheapit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;

public class DumbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dumb);
        Bundle extras = getIntent().getExtras();
        String first_name = "bda";
        if (extras != null) {
            first_name = extras.getString("name");
        }

        Button b = (Button)findViewById(R.id.dumb_logout);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent login = new Intent(DumbActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

        TextView t = (TextView)findViewById(R.id.dumb_first_name);
        t.setText(first_name);
    }
}
