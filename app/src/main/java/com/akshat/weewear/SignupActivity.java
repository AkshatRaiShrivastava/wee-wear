package com.akshat.weewear;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {


    Button next_btn;
    EditText name, phone, city, country, email,password, confirm_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        next_btn = findViewById(R.id.next_btn);
        name = findViewById(R.id.name_edit_text);
        phone = findViewById(R.id.mob_num_edit_text);
        city = findViewById(R.id.city_edit_text);
        country = findViewById(R.id.country_edit_text);
        password = findViewById(R.id.password_edit_text);
        confirm_password = findViewById(R.id.password_confirm_edit_text);
        email = findViewById(R.id.email_edit_text);


        next_btn.setOnClickListener(v->{

            String email_text = email.getText().toString();
            String password_text = password.getText().toString();
            String phone_number = phone.getText().toString();
            String name_text = name.getText().toString();
            String city_text = city.getText().toString();
            String country_text = country.getText().toString();

            if (password_text.equals(confirm_password.getText().toString())) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email_text, password_text).addOnSuccessListener(command -> {


                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name_text);
                    user.put("email", email_text);
                    user.put("phone", phone_number);
                    user.put("city", city_text);
                    user.put("country", country_text);
                    user.put("imageUrl", "value");
                    user.put("likedPosts", FieldValue.arrayUnion());

                    FirebaseFirestore.getInstance().collection("userData").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(SignupActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            });

                }).addOnFailureListener(command -> {
                    Toast.makeText(this, "Failed to create an account", Toast.LENGTH_SHORT).show();
                });

            }
            else {
                confirm_password.setError("Password does not match");
            }

        });

    }

}