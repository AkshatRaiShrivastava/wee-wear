package com.akshat.weewear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.akshat.weewear.databinding.ActivityOnBoardingBinding;

public class OnBoardingActivity extends AppCompatActivity {
    int nextBtnCounter;
    ActivityOnBoardingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager().beginTransaction().replace(R.id.onboarding_frame_layout, new OnboardingFragment1()).commit();
        nextBtnCounter = 0;

        binding.nextBtn.setOnClickListener(v->{
            nextBtnCounter++;
            nextBtnTasks();
        });
        binding.skipTextview.setOnClickListener(v->{
            startActivity(new Intent(OnBoardingActivity.this, LoginActivity.class));
            finish();
        });
    }
    void nextBtnTasks(){
        if (nextBtnCounter == 1){
            binding.dot1.setImageResource(R.drawable.unselect_dot);
            binding.dot2.setImageResource(R.drawable.selected_dot);
            getSupportFragmentManager().beginTransaction().replace(R.id.onboarding_frame_layout, new OnboardingFragment2()).commit();
            binding.nextBtn.setText("get started");
        }
        else if (nextBtnCounter == 2){
            startActivity(new Intent(OnBoardingActivity.this, LoginActivity.class));
            finish();
        }
    }
}