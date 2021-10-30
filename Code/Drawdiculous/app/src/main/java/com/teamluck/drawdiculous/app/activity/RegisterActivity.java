package com.teamluck.drawdiculous.app.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.model.User;
import com.teamluck.drawdiculous.app.utils.AppConst;
import com.teamluck.drawdiculous.app.utils.SafeOnClickListener;

import java.util.Objects;

/**
 * Register
 */
public class RegisterActivity extends AppCompatActivity {
    
    private static final String TAG = RegisterActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Log.d(TAG, "null");
        }
        else {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        findViewById(R.id.register_button).setOnClickListener(new SafeOnClickListener() {
            @Override
            public void onOneClick(View v) {
                register();
            }
        });
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, String.valueOf(item.getItemId()));
            finish();
        }
        return true;
    }
    
    /**
     * Register new account using Firebase API.
     */
    private void register() {
        EditText userNameInput = findViewById(R.id.register_username_text);
        EditText emailAddressInput = findViewById(R.id.register_email_text);
        EditText passwordInput = findViewById(R.id.register_password_text);
        
        String userName = userNameInput.getText().toString().trim();
        String emailAddress = emailAddressInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        
        fAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = User.getInstance();
                
                user.generateId();
                user.setUsername(userName);
                user.setEmailAddress(emailAddress);
                
                FirebaseDatabase fDatabase = FirebaseDatabase.getInstance(AppConst.DB_URL);
                DatabaseReference uRef = fDatabase.getReference("Users");
                FirebaseUser fUser = fAuth.getCurrentUser();
                assert fUser != null;
                
                uRef.child(fUser.getUid()).setValue(user).addOnCompleteListener(databaseTask -> {
                    if (databaseTask.isSuccessful()) {
                        Toast.makeText(this, R.string.regiSuccess, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, R.string.regiFail, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                try {
                    Log.d(TAG, "register: registration failed, handling error");
                    throw Objects.requireNonNull(task.getException());
                }
                catch (FirebaseAuthWeakPasswordException e) {
                    Log.d(TAG, "register: registration failed, please use a stronger password");
                    Toast.makeText(this, R.string.regiFailWeakPw, Toast.LENGTH_SHORT).show();
                }
                catch (FirebaseAuthUserCollisionException e) {
                    Log.d(TAG, "register: registration failed, email is already used");
                    Toast.makeText(this, R.string.regiFailEmailExist, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Log.d(TAG, "register: registration failed, unknown error");
                    Toast.makeText(this, R.string.regiFail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
