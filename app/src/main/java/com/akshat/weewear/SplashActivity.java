package com.akshat.weewear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    Animation fadeInAnimation;
    private boolean isAppInBackground = false; // Flag to track app state
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.splash_screen_logo);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo.startAnimation(fadeInAnimation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAppInBackground){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
        else {
            // App is launching fresh, show splash screen
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    if (sharedPreferences.getBoolean("isLoggedIn", true)) {
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
                        startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                        finish();
                    }

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();

                        // User is logged in, proceed to MainActivity
                    } else {


                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                        // User is not logged in, show LoginActivity
                    }
//                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                    finish();
                }
            }, 2000); // Delay for 2 seconds
        }

    }
}