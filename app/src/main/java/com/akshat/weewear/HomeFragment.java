package com.akshat.weewear;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akshat.weewear.adapters.SectionAdapter;
import com.akshat.weewear.adapters.SectionItemListAdapter;
import com.akshat.weewear.databinding.ActivityMainBinding;
import com.akshat.weewear.model.DataModel;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.Any;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    Spinner age_spinner, gender_spinner, location_spinner;
    TextView welcome_text, trending_heading, teenager_heading;
    ImageSlider carousel;
    RecyclerView trending_recycler_view, teenager_recycler_view;
    ActivityMainBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    RelativeLayout trending_main_layout, teenager_main_layout;
    public HomeFragment(){

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        trending_recycler_view = view.findViewById(R.id.trending_recycler_view);
        teenager_recycler_view = view.findViewById(R.id.teenager_recycler_view);
        welcome_text = view.findViewById(R.id.welcome_text);
        trending_heading = view.findViewById(R.id.trendingTitle);
        teenager_heading = view.findViewById(R.id.teenagerTitle);
        trending_main_layout = view.findViewById(R.id.trending_main_layout);
        teenager_main_layout = view.findViewById(R.id.teenager_main_layout);
        carousel = view.findViewById(R.id.carousel);
        ScrollView home_scroll_view = view.findViewById(R.id.home_scroll_view);
        age_spinner = view.findViewById(R.id.spinner_age);
        gender_spinner = view.findViewById(R.id.spinner_gender);
        location_spinner = view.findViewById(R.id.spinner_location);

        ArrayList<SlideModel> imageList = new ArrayList<>();
        db.collection("userData").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    return;
                }
                try {
                Object name = value.get("name");
                welcome_text.setText("Welcome "+name.toString() + "!");
                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });



        db.collection("images").document("carousel_images").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                imageList.clear();
                ArrayList<Any> items = (ArrayList<Any>) value.get("imageUrl");
                String image1 = String.valueOf(items.get(0));
                String image2 = String.valueOf(items.get(1));
                String image3 = String.valueOf(items.get(2));
                String image4 = String.valueOf(items.get(3));
                imageList.add(new SlideModel(image1, ScaleTypes.FIT));
                imageList.add(new SlideModel(image2, ScaleTypes.FIT));
                imageList.add(new SlideModel(image3, ScaleTypes.FIT));
                imageList.add(new SlideModel(image4, ScaleTypes.FIT));
                carousel.setImageList(imageList);
            }
        });

        setupSections("trending_section",trending_main_layout,  trending_recycler_view, trending_heading);
        setupSections("teenager_section", teenager_main_layout, teenager_recycler_view, teenager_heading);
//        setupTeenagerSection();

        return view;
    }


    void setupSections(String sectionName,RelativeLayout main_layout, RecyclerView recyclerView, TextView heading){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        List<DataModel> bannerList = new ArrayList<>();
        SectionAdapter sectionAdapter = new SectionAdapter(bannerList, getContext());
        recyclerView.setAdapter(sectionAdapter);

        db.collection(sectionName).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                bannerList.clear();
                for (DocumentSnapshot snapshot : value.getDocuments()){
//                    Toast.makeText(getContext(), snapshot.getId(), Toast.LENGTH_SHORT).show();
                    DataModel banner = snapshot.toObject(DataModel.class);
                    bannerList.add(banner);
//                    Toast.makeText(getContext(), banner.getTitle(), Toast.LENGTH_SHORT).show();
                }

                main_layout.setOnClickListener(v->{
                    Intent intent = new Intent(getContext(), SectionItemListActivity.class);
                    intent.putExtra("sectionName", sectionName);
                    intent.putExtra("sectionHeading", heading.getText().toString());
                    startActivity(intent);
                });

                sectionAdapter.notifyDataSetChanged();
            }
        });
    }

//    void setupTeenagerSection(){
//        teenager_recycler_view.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
//        List<DataModel> bannerList = new ArrayList<>();
//        SectionAdapter sectionAdapter = new SectionAdapter(bannerList, getContext());
//        teenager_recycler_view.setAdapter(sectionAdapter);
//
//        db.collection("teenager_section").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error!=null){
//                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                bannerList.clear();
//                for (DocumentSnapshot snapshot : value.getDocuments()){
////                    Toast.makeText(getContext(), snapshot.getId(), Toast.LENGTH_SHORT).show();
//                    DataModel banner = snapshot.toObject(DataModel.class);
//                    bannerList.add(banner);
////                    Toast.makeText(getContext(), banner.getTitle(), Toast.LENGTH_SHORT).show();
//                }
//                sectionAdapter.notifyDataSetChanged();
//            }
//        });
//    }
}