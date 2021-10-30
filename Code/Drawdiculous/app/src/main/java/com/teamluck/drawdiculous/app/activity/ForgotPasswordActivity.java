package com.teamluck.drawdiculous.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.teamluck.drawdiculous.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextView message;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        message = findViewById(R.id.message);
        
        Button forgetPasswordBtn = findViewById(R.id.recover);
        forgetPasswordBtn.setOnClickListener(v1 -> resetPassword());
    }
    
    public void resetPassword() {
        
        FirebaseAuth auth = FirebaseAuth.getInstance();
        
        EditText emailInput = findViewById(R.id.forgot_email_text);
        String email = emailInput.getText().toString().trim();
        
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    public void recover(View view) {
        message.setVisibility(View.VISIBLE);
    }
}
