package com.teamluck.drawdiculous.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.utils.AppConst;

/**
 * Splash activity, displays the splash page for users when user opens the application.
 * Loads the user login status.
 */
public class SplashActivity extends AppCompatActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Fires the next activity after SPLASH_DISPLAY_LENGTH
        new Handler().postDelayed(() -> {
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            Intent intent;
            if (fAuth.getCurrentUser() != null) {
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            }
            else {
                intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            }
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();
        }, AppConst.SPLASH_DISPLAY_LENGTH);
    }
}