package com.akshat.weewear;

import static android.text.TextUtils.indexOf;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.akshat.weewear.adapters.PostAdapter;
import com.akshat.weewear.databinding.ActivityLikedPostsBinding;
import com.akshat.weewear.model.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LikedPostsActivity extends AppCompatActivity {
    ActivityLikedPostsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLikedPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v->{
           getOnBackPressedDispatcher().onBackPressed();
        });
        setupLikedPosts();
    }
    void setupLikedPosts(){
        binding.likedPostRecyclerView.setLayoutManager(new LinearLayoutManager(LikedPostsActivity.this, LinearLayoutManager.VERTICAL,true));
        List<PostModel> postList = new ArrayList<>();
        PostAdapter sectionAdapter = new PostAdapter(postList, LikedPostsActivity.this);
        binding.likedPostRecyclerView.setAdapter(sectionAdapter);

        FirebaseFirestore.getInstance().collection("userData").document(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                .get().addOnSuccessListener(command->{
                    List<String> likedPosts = (List<String>) command.get("likedPosts");
                    if (likedPosts == null){
                        Toast.makeText(LikedPostsActivity.this, "No liked posts found !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (String postId : likedPosts){
                    FirebaseFirestore.getInstance().collection("posts").document(postId)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().exists()){
                                        PostModel post = task.getResult().toObject(PostModel.class);
                                        postList.add(post);
                                        sectionAdapter.notifyDataSetChanged();

                                        }
                                        else {
                                            Toast.makeText(LikedPostsActivity.this, "No liked posts found !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                    }
                });

    }
}