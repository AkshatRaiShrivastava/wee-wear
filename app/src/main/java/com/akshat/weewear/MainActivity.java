package com.akshat.weewear;

import static com.akshat.weewear.R.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.akshat.weewear.databinding.FragmentHomeBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity  {
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user_profile/"+userId);
    Uri profileImageUri;
    HomeFragment homeFragment;
    SearchFragment searchFragment;
    FeedFragment feedFragment;
    ActivityFragment activityFragment;
    MyProfileFragment myProfileFragment;
    DrawerLayout drawer_layout;
    TextView username;
    BottomNavigationView bottomNavigationView;
    ImageView menu_icon, user_icon,profileImageView;
    RelativeLayout header;
    FrameLayout main_frame_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        main_frame_layout = findViewById(id.main_frame_layout);
        bottomNavigationView = findViewById(id.bottom_navigation);
        menu_icon = findViewById(id.menu_icon);
        user_icon = findViewById(id.userIcon);
        drawer_layout = findViewById(id.drawerLayout);
        homeFragment = new HomeFragment();
        header = findViewById(id.header);
        myProfileFragment = new MyProfileFragment();
        searchFragment = new SearchFragment();
        feedFragment = new FeedFragment();
        activityFragment = new ActivityFragment();


        NavigationView navigationView = (NavigationView) findViewById(id.navMenu);
        View headerView = navigationView.getHeaderView(0);
        profileImageView = (ImageView) headerView.findViewById(id.user_icon);
        username = (TextView) headerView.findViewById(id.username_text);




//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadUserProfile();
//            }
//        });



        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == id.nav_logout){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Are you sure you want to logout ?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    GoogleSignInOptions gso = new GoogleSignInOptions.
                                            Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                            build();

                                    GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(MainActivity.this,gso);
                                    googleSignInClient.signOut();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
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
                if (item.getItemId() == id.nav_account){
                    bottomNavigationView.setSelectedItemId(id.nav_profile);
                    getSupportFragmentManager().beginTransaction().replace(id.main_frame_layout, myProfileFragment).commit();
                    header.setVisibility(View.GONE);

                    drawer_layout.closeDrawer(GravityCompat.START);
                }
                if (item.getItemId() == id.nav_liked_post){
                    startActivity(new Intent(MainActivity.this, LikedPostsActivity.class));
                }
                if (item.getItemId() == id.nav_create_post){
                    startActivity(new Intent(MainActivity.this, CreatePostActivity.class));
                }
                if(item.getItemId() == id.nav_my_posts){
                    bottomNavigationView.setSelectedItemId(id.nav_profile);
                    getSupportFragmentManager().beginTransaction().replace(id.main_frame_layout, myProfileFragment).commit();
                    header.setVisibility(View.GONE);
                }
                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == id.nav_home){
                    try {
                    getSupportFragmentManager().beginTransaction().replace(id.main_frame_layout, homeFragment).commit();
                    header.setVisibility(View.VISIBLE);
                    }catch (Exception e){
                        Log.d("Exception", e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                if (item.getItemId() == id.nav_search){
                    getSupportFragmentManager().beginTransaction().replace(id.main_frame_layout, searchFragment).commit();
                    header.setVisibility(View.GONE);
                }
                if (item.getItemId() == id.nav_feed){
                    getSupportFragmentManager().beginTransaction().replace(id.main_frame_layout, feedFragment).commit();
                    header.setVisibility(View.GONE);
                }
                if (item.getItemId() == id.nav_activity){
                    getSupportFragmentManager().beginTransaction().replace(id.main_frame_layout, activityFragment).commit();
                    header.setVisibility(View.GONE);
                }
                if (item.getItemId() == id.nav_profile){
                    getSupportFragmentManager().beginTransaction().replace(id.main_frame_layout, myProfileFragment).commit();
                    header.setVisibility(View.GONE);
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(id.nav_home);

        db.collection("userData")
                .document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                            Object name = value.get("name");
                            Object imageUrl = value.get("imageUrl");
                            username.setText(name.toString());
                            Toast.makeText(MainActivity.this, imageUrl != null ? imageUrl.toString() : null, Toast.LENGTH_SHORT).show();
                            Glide.with(MainActivity.this).load(imageUrl != null ? imageUrl.toString() : null).circleCrop().error(drawable.account_icon).into(user_icon);
                            Glide.with(MainActivity.this).load(imageUrl != null ? imageUrl.toString() : null).circleCrop().error(drawable.account_icon).into(profileImageView);
//                        }catch (Exception ex){
//                            Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
    }




}