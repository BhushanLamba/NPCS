package wts.com.npcs.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import soup.neumorphism.NeumorphCardView;
import wts.com.npcs.R;
import wts.com.npcs.dashboardFragments.HomeFragment;

public class LoginActivity extends AppCompatActivity {

    NeumorphCardView loginCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginCard=findViewById(R.id.login_card);

        loginCard.setOnClickListener(v->
        {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });
    }
}