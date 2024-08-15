package com.trengginas.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trengginas.R;
import com.trengginas.activity.DatatableActivity;
import com.trengginas.activity.InfografisActivity;
import com.trengginas.activity.KontakActivity;
import com.trengginas.activity.MaswilActivity;
import com.trengginas.activity.PublikasiActivity;
import com.trengginas.activity.TentangActivity;
import com.trengginas.model.Home;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<Home> homeList;
    private Context context;

    public HomeAdapter(Context context, List<Home> homeList) {
        this.context = context;
        this.homeList = homeList;
    }

    @Override
    public int getItemViewType(int position) {
        return homeList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            return new HomeViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HomeViewHolder) {
            Home home = homeList.get(position);
            HomeViewHolder homeViewHolder = (HomeViewHolder) holder;

            homeViewHolder.titleTextView.setText(home.getJudul());
            homeViewHolder.subtitleTextView.setText(home.getSubjudul());

            String imageUrl = home.getIcon().replace("jokosanbps.com", "bpstrenggalek.com");
            Glide.with(context).load(imageUrl).into(homeViewHolder.iconImageView);

            homeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nmscreen = home.getNmscreen();
                    if (nmscreen == null) {
                        Toast.makeText(context, "Screen name is null", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent;

                    switch (nmscreen) {
                        case "datatabel":
                            intent = new Intent(context, DatatableActivity.class);
                            break;
                        case "infografis":
                            intent = new Intent(context, InfografisActivity.class);
                            break;
                        case "Publikasi":
                            intent = new Intent(context, PublikasiActivity.class);
                            break;
                        case "maswil":
                            intent = new Intent(context, MaswilActivity.class);
                            break;
                        case "tentang_bps":
                            intent = new Intent(context, TentangActivity.class);
                            break;
                        case "kontak":
                            intent = new Intent(context, KontakActivity.class);
                            break;
                        default:
                            Toast.makeText(context, "Screen not found", Toast.LENGTH_SHORT).show();
                            return;
                    }

                    context.startActivity(intent);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            // LoadingViewHolder: do nothing special, the loading indicator will be shown automatically
        }
    }

    @Override
    public int getItemCount() {
        return homeList == null ? 0 : homeList.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView subtitleTextView;
        ImageView iconImageView;
        CardView cardView;

        public HomeViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            subtitleTextView = itemView.findViewById(R.id.item_subtitle);
            iconImageView = itemView.findViewById(R.id.image_view);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void updateData(List<Home> homes) {
        this.homeList = homes;
        notifyDataSetChanged();
    }

    public void addLoadingFooter() {
        homeList.add(null);
        notifyItemInserted(homeList.size() - 1);
    }

    public void removeLoadingFooter() {
        int position = homeList.size() - 1;
        Home item = homeList.get(position);
        if (item == null) {
            homeList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
