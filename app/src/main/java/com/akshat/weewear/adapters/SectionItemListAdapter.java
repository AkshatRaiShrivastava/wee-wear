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

public class SectionItemListAdapter extends RecyclerView.Adapter<SectionItemListAdapter.SectionViewHolder> {
    List<DataModel> bannerList;
    private Context context;

    public SectionItemListAdapter(List<DataModel> bannerList, Context context) {
        this.bannerList = bannerList;
        this.context = context;
    }

    @NonNull
    @Override
    public SectionItemListAdapter.SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_list_item, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        DataModel banner = bannerList.get(position);
        holder.title_text.setText(banner.getTitle());
        holder.subtitle_text.setText(banner.getSubtitle());
        Glide.with(context).load(banner.geturl()).transform(new RoundedCorners(32)).into(holder.cover_image);

    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView title_text, subtitle_text;
        ImageView cover_image;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            title_text = itemView.findViewById(R.id.title_text_view);
            subtitle_text = itemView.findViewById(R.id.subtitle_text_view);
            cover_image = itemView.findViewById(R.id.cover_image_view);
        }
    }
}
