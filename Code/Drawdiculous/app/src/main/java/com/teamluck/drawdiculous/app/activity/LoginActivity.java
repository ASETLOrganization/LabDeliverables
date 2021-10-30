package com.teamluck.drawdiculous.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.utils.SafeOnClickListener;

/**
 * Login
 */
public class LoginActivity extends AppCompatActivity {
    
    public static final String TAG = LoginActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_button).setOnClickListener(new SafeOnClickListener() {
            @Override
            public void onOneClick(View v) {
                verifyLogin();
            }
        });
        
        findViewById(R.id.forget_password_button).setOnClickListener(new SafeOnClickListener() {
            
            @Override
            public void onOneClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
            
        });
    }
    
    /**
     * Verify login using firebase API.
     */
    private void verifyLogin() {
        EditText emailText = findViewById(R.id.login_email_text);
        EditText passwordText = findViewById(R.id.login_password_text);
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                Toast.makeText(this, R.string.loginSuccess, Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    Exception exception = task.getException();
                    assert exception != null;
                    throw exception;
                }
                catch (FirebaseAuthInvalidUserException e) {
                    Toast.makeText(this, R.string.loginFailUser, Toast.LENGTH_SHORT).show();
                }
                catch (FirebaseAuthInvalidCredentialsException e) {
                    Toast.makeText(this, R.string.loginFailPw, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(this, R.string.loginFailUnknown, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}