package com.akshat.weewear.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akshat.weewear.R;
import com.akshat.weewear.model.DataModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
    List<DataModel> bannerList;
    private Context context;

    public SectionAdapter(List<DataModel> bannerList, Context context) {
        this.bannerList = bannerList;
        this.context = context;
    }

    @NonNull
    @Override
    public SectionAdapter.SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_items, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        DataModel banner = bannerList.get(position);
        holder.trending_title_text.setText(banner.getTitle());
        holder.trending_subtitle_text.setText(banner.getSubtitle());
        Glide.with(context).load(banner.geturl()).transform(new RoundedCorners(32)).into(holder.cover_image);

    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder{
        TextView trending_title_text, trending_subtitle_text;
        ImageView cover_image;
        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            trending_title_text = itemView.findViewById(R.id.title_text);
            trending_subtitle_text = itemView.findViewById(R.id.subtitle_text);
            cover_image= itemView.findViewById(R.id.cover_image);
        }
    }
}
