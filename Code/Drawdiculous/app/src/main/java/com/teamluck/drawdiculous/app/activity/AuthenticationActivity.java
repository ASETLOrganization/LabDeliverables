package com.teamluck.drawdiculous.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.utils.SafeOnClickListener;

/**
 * Authentication activity, shown on start of application.
 * Asks the user to choose between Login and Register.
 * TODO: Logout, Forget Password
 */
public class AuthenticationActivity extends AppCompatActivity {
    
    private static final String TAG = AuthenticationActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Log.d(TAG, "null");
        }
        else {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        // redirects to login activity
        findViewById(R.id.authentication_login_button).setOnClickListener(new SafeOnClickListener() {
            @Override
            public void onOneClick(View v) {
                startActivity(new Intent(AuthenticationActivity.this, LoginActivity.class));
            }
        });
        
        // redirects to register activity
        findViewById(R.id.authentication_register_button).setOnClickListener(new SafeOnClickListener() {
            @Override
            public void onOneClick(View v) {
                startActivity(new Intent(AuthenticationActivity.this, RegisterActivity.class));
            }
        });
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}