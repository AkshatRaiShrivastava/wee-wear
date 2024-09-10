package com.akshat.weewear;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.akshat.weewear.adapters.PostAdapter;
import com.akshat.weewear.adapters.SectionItemListAdapter;
import com.akshat.weewear.databinding.FragmentFeedBinding;
import com.akshat.weewear.model.DataModel;
import com.akshat.weewear.model.PostModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
    ImageView user_icon, add_icon;
    Object ImageUrl;
    RecyclerView postRecyclerView;
    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        user_icon = view.findViewById(R.id.userIcon);
        add_icon = view.findViewById(R.id.add_icon);
        postRecyclerView = view.findViewById(R.id.post_recycler_view);
        add_icon.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), CreatePostActivity.class);
            intent.putExtra("user_image_url", ImageUrl.toString());
            startActivity(intent);
        });
        try {

        FirebaseFirestore.getInstance().collection("userData").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                   ImageUrl = value.get("imageUrl");
                    Glide.with(getContext()).load(ImageUrl.toString()).error(R.drawable.account_icon).circleCrop().into(user_icon);
                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        setupPosts();
        return view;
    }
    void setupPosts(){
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        List<PostModel> postList = new ArrayList<>();
        PostAdapter sectionAdapter = new PostAdapter(postList, getContext());
        postRecyclerView.setAdapter(sectionAdapter);

        FirebaseFirestore.getInstance().collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                postList.clear();
                for (DocumentSnapshot snapshot : value.getDocuments()){
                    PostModel post = snapshot.toObject(PostModel.class);
                    postList.add(post);

                }
                sectionAdapter.notifyDataSetChanged();
            }
        });
    }
}