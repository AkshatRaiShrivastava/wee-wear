package com.akshat.weewear;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.akshat.weewear.databinding.ActivityCreatePostBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    String title, subtitle, username;
    ActivityCreatePostBinding binding;
    Map<String, Object> data = new HashMap<>();
    String imageEncoded;
    TextView total;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
    Uri selectedImageUri;
    ArrayList<String> downloadUri;
    int position = 0;
    List<String> imagesEncodedList;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        LocalDateTime now = LocalDateTime.now();
        String date_time = dtf.format(now).toString();
        Intent intent = getIntent();
        String user_image_url = intent.getStringExtra("user_image_url");
        FirebaseFirestore.getInstance().collection("userData").document(userId).get().addOnSuccessListener(command -> {
                    username = command.get("name").toString();
                });
        binding.backBtn.setOnClickListener(v->{
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.selectImagesBtn.setOnClickListener(v->{
            Intent imageintent = new Intent();
            imageintent.setType("image/*");
            imageintent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(imageintent,"Select Picture"), 1);
        });

        binding.postBtn.setOnClickListener(v->{
            title = binding.titleEditText.getText().toString();
            subtitle = binding.subtitleEditText.getText().toString();
            ProgressDialog progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
            progressDialog.setTitle("Creating post");

            progressDialog.show();
            FirebaseStorage.getInstance().getReference().child("post_images/"+userId+"_"+date_time+"/image").putFile(selectedImageUri)
                            .addOnCompleteListener(task->{
                                if (task.isSuccessful()){
                                    FirebaseStorage.getInstance().getReference().child("post_images/"+userId+"_"+date_time+"/image").getDownloadUrl().addOnSuccessListener(command -> {
                                        db.collection("posts").document(userId+"_"+date_time).set(new HashMap<String ,Object>(){{
                                            put("username", username);
                                            put("email", email);
                                            put("title", title);
                                            put("user_image_url", user_image_url);
                                            put("subtitle", subtitle);
                                            put("userId", userId);
                                            put("postUrl", command.toString());
                                            put("postId", userId+"_"+date_time);
                                        }}).addOnSuccessListener(command1 -> {
                                            FirebaseFirestore.getInstance().collection("userData").document(userId).update("userPosts", FieldValue.arrayUnion(userId+"_"+date_time));
                                            Toast.makeText(CreatePostActivity.this, "Post Created", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            getOnBackPressedDispatcher().onBackPressed();
                                        }).addOnFailureListener(command2 -> {
                                            Toast.makeText(CreatePostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        });
                                    });
                                }
                            });


//            for (int i = 0; i < mArrayUri.size(); i++) {
//                int finalI = i;
//                FirebaseStorage.getInstance().getReference().child("post_images/"+userId+"_"+date_time+"/image"+finalI).putFile(mArrayUri.get(i))
//                        .continueWithTask(command -> {
//                             if (!command.isSuccessful()){
//                                 throw command.getException();
//                             }
//                             return FirebaseStorage.getInstance().getReference().child("post_images/"+userId+"_"+date_time+"/image"+finalI).getDownloadUrl();
//                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//                                if (task.isSuccessful()){
//                                    data.put("imageUrls", FieldValue.arrayUnion(task.getResult().toString()));
//                                    db.collection("posts").document(userId+"_"+date_time).update(data);
//                                }
//                            }
//                        });
//            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){

            selectedImageUri = data.getData();
            binding.imageSwitcher.setImageURI(selectedImageUri);

        }

    }

}
