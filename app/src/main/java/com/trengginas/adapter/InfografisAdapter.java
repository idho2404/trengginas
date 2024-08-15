// InfografisAdapter.java
package com.trengginas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.trengginas.R;
import java.util.List;

public class InfografisAdapter extends RecyclerView.Adapter<InfografisAdapter.InfografisViewHolder> {

    private final Context context;
    private final List<String> imageList;

    public InfografisAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public InfografisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_infografis, parent, false);
        return new InfografisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfografisViewHolder holder, int position) {
        String imageUrl = imageList.get(position);
        Glide.with(context).load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class InfografisViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public InfografisViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
