package com.akshat.weewear;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button reset_btn;
    EditText emailId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailId = findViewById(R.id.email_phone_edit_text);
        reset_btn = findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(v-> {
            String email = emailId.getText().toString();
            if(email.isEmpty()){
                emailId.setError("Email is required");
                return;
            }
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailId.setError("Invalid email");
                return;
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
}