package com.akshat.weewear;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akshat.weewear.adapters.PostAdapter;
import com.akshat.weewear.databinding.FragmentMyProfileBinding;
import com.akshat.weewear.model.PostModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MyProfileFragment extends Fragment  {
    FragmentMyProfileBinding binding;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String imageUrl;
    LinearProgressIndicator progress_bar;
    Boolean edit_profile;
    Uri selectedImageUri;
    int SELECT_PICTURE = 200;
    String profileImageUriText ;
    HomeFragment homeFragment;
    EditText usernameEdit, phoneEdit, cityEdit, countryEdit;
    BottomNavigationView bottomNavigationView;
    ImageView back_icon, userImage;
    TextView usernameText, userEmail,userPhone;
    ExtendedFloatingActionButton edit_btn;
    public MyProfileFragment() {
        // Required empty public constructor
    }

    @SuppressLint("CheckResult")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        binding = FragmentMyProfileBinding.bind(view);
        back_icon = view.findViewById(R.id.back_btn);
        userPhone = view.findViewById(R.id.userPhone);
        userImage = view.findViewById(R.id.userImage);
        progress_bar = view.findViewById(R.id.progress_bar);
        edit_btn = view.findViewById(R.id.edit_btn);
        usernameText = view.findViewById(R.id.usernameText);
        userEmail = view.findViewById(R.id.userEmail);
        homeFragment = new HomeFragment();
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        usernameEdit = view.findViewById(R.id.usernameEdit);
        phoneEdit = view.findViewById(R.id.phoneEdit);
        cityEdit = view.findViewById(R.id.cityEdit);
        countryEdit = view.findViewById(R.id.countryEdit);

        edit_profile = false;
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, homeFragment).commit();
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        });


        binding.delAccountBtn.setOnClickListener(v->{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage("Are you sure you want to delete your account ?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete().addOnSuccessListener(command -> {
                               startActivity(new Intent(getContext(), LoginActivity.class));
                            });
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        });


            FirebaseFirestore.getInstance().collection("userData")
                    .document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error!= null){
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else {
                                progress_bar.setVisibility(View.VISIBLE);
                                try {
                                    Object name = value.get("name");
                                    usernameText.setText(name.toString());
                                    usernameEdit.setText(name.toString());
                                    Object email = value.get("email");
                                    userEmail.setText(email.toString());
                                    Object phone = value.get("phone");
                                    userPhone.setText(phone.toString());
                                    phoneEdit.setText(phone.toString());
                                    Object city = value.get("city");
                                    cityEdit.setText(city.toString());
                                    Object country = value.get("country");
                                    countryEdit.setText(country.toString());
                                    Object username = value.get("name");
                                    usernameEdit.setText(username.toString());
                                    Object ImageUrl = value.get("imageUrl");
                                    imageUrl = ImageUrl.toString();
                        Toast.makeText(getContext(), imageUrl, Toast.LENGTH_SHORT).show();
                                    Glide.with(MyProfileFragment.this).load(imageUrl).circleCrop().error(R.drawable.account_icon).into(userImage);

                                }catch (Exception ex){
                                    Toast.makeText(getContext(), ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            progress_bar.setVisibility(View.INVISIBLE);
                        }
                    });





        userImage.setOnClickListener(v-> {
            if (edit_profile){
//                ImagePicker.with(this)
//                        .cropSquare()
//                        .compress(150)
//                        .start();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
            }

        });

        edit_btn.setOnClickListener(v->{
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Updating Profile");
            if (edit_profile){

                progressDialog.show();
                try {

                    FirebaseStorage.getInstance().getReference().child("user_profile/"+userId).getDownloadUrl().addOnCompleteListener(v1->{
                        if (v1.isSuccessful()){
                            profileImageUriText = v1.getResult().toString();
                            FirebaseFirestore.getInstance().collection("userData")
                                    .document(userId)
                                    .update("name", usernameEdit.getText().toString(), "phone", phoneEdit.getText().toString(), "city", cityEdit.getText().toString(), "country", countryEdit.getText().toString(),"country", countryEdit.getText().toString(), "imageUrl", profileImageUriText)
                                    .addOnSuccessListener(command ->{
                                        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                        edit_profile = false;
                                        usernameEdit.setEnabled(false);
                                        phoneEdit.setEnabled(false);
                                        cityEdit.setEnabled(false);
                                        countryEdit.setEnabled(false);
                                        edit_btn.setText("Edit Profile");
                                        edit_btn.setIconResource(R.drawable.edit_icon);
                                    }).addOnFailureListener(v2->{
                                        Toast.makeText(getContext(), v2.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            edit_profile = false;
                            Toast.makeText(getContext(), profileImageUriText, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(task->{
//                        Toast.makeText(getContext(), task.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        progressDialog.dismiss();
                    }).addOnFailureListener(command -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), command.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }catch(Exception ex){
                    Toast.makeText(getContext(), ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
            else{
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Do you want to edit your profile ?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                edit_profile = true;
                                usernameEdit.setEnabled(true);
                                phoneEdit.setEnabled(true);
                                cityEdit.setEnabled(true);
                                countryEdit.setEnabled(true);
                                edit_btn.setText("Done");
                                edit_btn.setIconResource(R.drawable.done_icon);
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
//            Toast.makeText(getContext(), "Edit Profile", Toast.LENGTH_SHORT).show();


        });

        setupUserPosts();

        return view;
    }
//    void loadUserImage(){
//        usernameText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
//        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
//        FirebaseStorage.getInstance().getReference()
//                .child("user_profile/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
//                .getDownloadUrl().addOnCompleteListener(t->{
//                    if (t.isSuccessful()){
//                        profileImageUri = t.getResult();
//                        Glide.with(this).load(profileImageUri).circleCrop().into(userImage);
//                    }
//                    else {
//                        Toast.makeText(getContext(), t.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        userImage.setImageResource(R.drawable.account_icon);
//                    }
//                });

//    }


    void setupUserPosts(){
        binding.myPostsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        List<PostModel> postList = new ArrayList<>();
        PostAdapter sectionAdapter = new PostAdapter(postList, getContext());
        binding.myPostsRecyclerView.setAdapter(sectionAdapter);

        FirebaseFirestore.getInstance().collection("userData").document(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                .get().addOnSuccessListener(command->{
                    List<String> userPosts = (List<String>) command.get("userPosts");
                    if (userPosts == null){
                        Toast.makeText(getContext(), "No liked posts found !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (String postId : userPosts){
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
                                                Toast.makeText(getContext(), "No liked posts found !", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });

                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant

            try {
                selectedImageUri = data.getData();
            Toast.makeText(getActivity(), selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
                userImage.setImageURI(selectedImageUri);
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Uploading image, please wait");
                progressDialog.setCancelable(false);
                progressDialog.show();
                FirebaseStorage.getInstance().getReference().child("user_profile/" + userId)
                        .putFile(selectedImageUri).addOnSuccessListener(v->{
                            Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        });


//                    FirebaseStorage.getInstance().getReference().child("user_profile/" + userId)
//                            .putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    if (taskSnapshot.getTask().isSuccessful()){
//                                        Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
//                                        userImage.setImageURI(selectedImageUri);
//
//                                    }
//                                    else {
//                                        Log.d("Exception", taskSnapshot.getTask().getResult().toString());
//                                    }
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    Log.d("Exception", e.getMessage());
//                                }
//                            });



//        catch (Exception ex){
//                    Toast.makeText(getActivity(), ex.getMessage().toString(),Toast.LENGTH_SHORT).show();
//                    Log.d("Exception", ex.getMessage());
//                }


//                        FirebaseStorage.getInstance().getReference().child("user_profile/"+userId).getDownloadUrl().addOnCompleteListener(v->{
//                            imageUrl = v.getResult().toString();
//                            FirebaseFirestore.getInstance().collection("userData").document(userId)
//                                    .update("imageUrl", imageUrl).addOnSuccessListener(command->{
//                                        Toast.makeText(getContext(), "Image uploaded" , Toast.LENGTH_SHORT).show();
//                                    });
////                        Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
////                    Toast.makeText(getContext(), imageUrl, Toast.LENGTH_SHORT).show();
//                        });
////                    }catch (Exception ex){
////                        Toast.makeText(getContext(), ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
////                    }

            }catch (Exception ex){
                Toast.makeText(getContext(), ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }



        }
    }
}