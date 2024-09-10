package com.akshat.weewear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    int RC_SIGN_IN = 20;
    SignInButton google_btn;
    SharedPreferences onBoardingScreen;
    EditText email_phone, password;
    TextView forgot_password;
    Button login_btn, signup_btn;
    ImageView google_login, facebook_login;
    LinearProgressIndicator progress_bar;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
//    private static final String TAG = "PhoneAuthActivity";
    private String VerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_phone = findViewById(R.id.email_phone_edit_text);
        password = findViewById(R.id.password_edit_text);
        forgot_password = findViewById(R.id.forgot_password);
        login_btn = findViewById(R.id.login_btn);
        signup_btn = findViewById(R.id.signup_btn);
        google_btn = findViewById(R.id.google_sign_in_button);
        google_login = findViewById(R.id.google_login);
        facebook_login = findViewById(R.id.facebook_login);
        progress_bar = findViewById(R.id.progress_bar);
        mAuth = FirebaseAuth.getInstance();
        final Boolean[] email_text = new Boolean[1];
        onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
        boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);

        if (isFirstTime){
            SharedPreferences.Editor editor = onBoardingScreen.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
            startActivity(new Intent(LoginActivity.this, OnBoardingActivity.class));
            finish();
        }
        email_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmail(email_phone.getText().toString())){
                    email_text[0] = true;
                } else if (isPhone(email_phone.getText().toString())) {
                    email_text[0] = false;
                }
                else {
                    email_phone.setError("Invalid email");
                }
            }
        });
        forgot_password.setOnClickListener(v->{
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        signup_btn.setOnClickListener(v->{
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google_btn.setOnClickListener(v->{
           googleSignIn();
        });

        login_btn.setOnClickListener(v -> {
            if (email_phone.getText().length() == 0){
                email_phone.setError("Invalid email");
                return;
            }
            if (email_text[0]){
                String email = email_phone.getText().toString();
                String Password = password.getText().toString();
                progress_bar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, Password).addOnSuccessListener(command -> {
                    Toast.makeText(this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                    progress_bar.setVisibility(View.GONE);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }).addOnFailureListener(command -> {
                    Toast.makeText(this, "User login failed", Toast.LENGTH_SHORT).show(); 
                });
                
            }
            else if(!email_text[0]) {



            }
        });


    }

    private void googleSignIn(){
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==  RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            }catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("name", user.getDisplayName());
                            map.put("email", user.getEmail());
                            map.put("phone", "null");
                            map.put("city", "null");
                            map.put("country", "null");
                            map.put("imageUrl", user.getPhotoUrl());
                            map.put("likedPosts", FieldValue.arrayUnion());

                            FirebaseFirestore.getInstance().collection("userData").document(user.getUid())
                                    .set(map);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    public static boolean isEmail(String text) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern p = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public static boolean isPhone(String text) {
        if(!TextUtils.isEmpty(text)){
            return TextUtils.isDigitsOnly(text);
        } else{
            return false;
        }
    }
}