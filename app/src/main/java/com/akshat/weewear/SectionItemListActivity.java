package com.akshat.weewear;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akshat.weewear.adapters.SectionAdapter;
import com.akshat.weewear.adapters.SectionItemListAdapter;
import com.akshat.weewear.databinding.ActivitySectionItemListBinding;
import com.akshat.weewear.model.DataModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SectionItemListActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String sectionName;
    ActivitySectionItemListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySectionItemListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        sectionName = intent.getStringExtra("sectionName");
        String sectionHeading = intent.getStringExtra("sectionHeading");
        binding.headingTextView.setText(sectionHeading);
        binding.backIcon.setOnClickListener(v->{
            getOnBackPressedDispatcher().onBackPressed();
        });
        setupItemRecyclerView();
    }
    void setupItemRecyclerView(){
        binding.sectionItemListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        List<DataModel> bannerList = new ArrayList<>();
        SectionItemListAdapter sectionAdapter = new SectionItemListAdapter(bannerList, this);
        binding.sectionItemListRecyclerView.setAdapter(sectionAdapter);

        db.collection(sectionName).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(SectionItemListActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                bannerList.clear();
                for (DocumentSnapshot snapshot : value.getDocuments()){
                    DataModel banner = snapshot.toObject(DataModel.class);
                    bannerList.add(banner);

                }
                sectionAdapter.notifyDataSetChanged();
            }
        });

    }
}