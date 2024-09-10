package com.akshat.weewear.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akshat.weewear.MyProfileFragment;
import com.akshat.weewear.R;
import com.akshat.weewear.model.DataModel;
import com.akshat.weewear.model.PostModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.denzcoskun.imageslider.ImageSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Any;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.SectionViewHolder> {
    List<PostModel> postModelList;
    private Context context;

    public PostAdapter(List<PostModel> postModelList, Context context) {
        this.postModelList = postModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostAdapter.SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        PostModel post = postModelList.get(position);
//        holder.title_text.setText(post.getTitle());
//        holder.subtitle_text.setText(banner.getSubtitle());
        holder.username_text.setText(post.getUsername());
        Glide.with(context).load(post.getUser_image_url()).circleCrop().into(holder.user_image);
        Glide.with(context).load(post.getPostUrl()).into(holder.post_image_carousel);
        holder.title.setText(post.getTitle());
        holder.subtitle.setText(post.getSubtitle());
        holder.three_dots.setOnClickListener(v-> {
            PopupMenu popupMenu = new PopupMenu(context, holder.three_dots);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.post_menu, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(v1->{
                if(v1.getItemId() == R.id.menu_delete){
                    FirebaseFirestore.getInstance().collection("posts").document(post.getPostId()).delete()
                        .addOnSuccessListener(v2->{
                            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                    });
}
                return false;
            });
        });
        holder.like_btn.setOnClickListener(v->{

                Map<String, Object> data = new HashMap<>();
                data.put("likedPosts", FieldValue.arrayUnion(post.getPostId()));
                FirebaseFirestore.getInstance().collection("userData").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update(data).addOnSuccessListener(command -> {
                            Toast.makeText(context, "Post liked", Toast.LENGTH_SHORT).show();
                            holder.like_btn.setVisibility(View.GONE);
                            holder.liked_btn.setVisibility(View.VISIBLE);
                        }).addOnFailureListener(command -> {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        });
        });
        holder.liked_btn.setOnClickListener(v->{

            Map<String, Object> data = new HashMap<>();
            data.put("likedPosts", FieldValue.arrayRemove(post.getPostId()));
            FirebaseFirestore.getInstance().collection("userData").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .update(data).addOnSuccessListener(command -> {
                        holder.like_btn.setVisibility(View.VISIBLE);
                        holder.liked_btn.setVisibility(View.GONE);
                    }).addOnFailureListener(command -> {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    });

        });
        FirebaseFirestore.getInstance().collection("userData").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(command -> {
                    List<String> likedPosts = (List<String>) command.get("likedPosts");
                    if (likedPosts.contains(post.getPostId())){
                        holder.like_btn.setVisibility(View.GONE);
                        holder.liked_btn.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder{
        TextView username_text, title, subtitle;
        ImageView user_image, post_image_carousel, like_btn, liked_btn,send_btn, three_dots;
        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.post_user_icon);
            post_image_carousel = itemView.findViewById(R.id.post_image_carousel);
            username_text = itemView.findViewById(R.id.username_text);
            like_btn = itemView.findViewById(R.id.like_btn);
            send_btn = itemView.findViewById(R.id.send_btn);
            three_dots = itemView.findViewById(R.id.three_dots_icon);
            liked_btn = itemView.findViewById(R.id.liked_btn);
            title = itemView.findViewById(R.id.title_text_view);
            subtitle = itemView.findViewById(R.id.subtitle_text_view);
        }
    }
}
